package com.appsdeveloperblog.store.OrdersService.saga;

import com.appsdeveloperblog.store.OrdersService.command.commands.ApproveOrderCommand;
import com.appsdeveloperblog.store.OrdersService.command.commands.RejectOrderCommand;
import com.appsdeveloperblog.store.OrdersService.core.events.OrderApprovedEvent;
import com.appsdeveloperblog.store.OrdersService.core.events.OrderCreatedEvent;
import com.appsdeveloperblog.store.OrdersService.core.events.OrderRejectedEvent;
import com.appsdeveloperblog.store.core.commands.CancelProductReservationCommand;
import com.appsdeveloperblog.store.core.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.store.core.commands.ReserveProductCommand;
import com.appsdeveloperblog.store.core.events.PaymentProcessedEvent;
import com.appsdeveloperblog.store.core.events.ProductReservationCancelledEvent;
import com.appsdeveloperblog.store.core.events.ProductReservedEvent;
import com.appsdeveloperblog.store.core.model.User;
import com.appsdeveloperblog.store.core.query.FetchUserPaymentDetailsQuery;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient CompensationTransaction compensationTransaction;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        LOGGER.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId() +
                " and productId: " + reserveProductCommand.getProductId());

        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    // start a compensanting transaction
                }
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        // Process user payment
        LOGGER.info("ProductReservedEvent is called for productId: " + productReservedEvent.getProductId() +
                " and orderId: " + productReservedEvent.getOrderId());

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery =
                new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception exception) {
            exception.printStackTrace();
            LOGGER.error(exception.getMessage());
            // start the compensation transaction
            compensationTransaction.cancelProductReservation(productReservedEvent, exception.getMessage());
            return;
        }

        if (userPaymentDetails == null) {
            // Start compensation transaction
            compensationTransaction.cancelProductReservation(productReservedEvent, "Could not fetch user payment details");
            return;
        }
        LOGGER.info("Successfully fetched user payment details for user " + userPaymentDetails.getFirstName());
        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;

        try {
            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            compensationTransaction.cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }

        if (result == null) {
            LOGGER.info("The ProcessPaymentCommand resulted null. Initiating a compensating transaction");
            // Start compensating transaction
            compensationTransaction.cancelProductReservation(productReservedEvent, "Could not proccess yser payment with provided payment details");

        }
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        // Send an ApprovedOrderCommand
        ApproveOrderCommand approveOrderCommand =
                new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.send(approveOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        LOGGER.info("Order is approved. Order Saga is complete for orderId: " + orderApprovedEvent.getOrderId());
        //SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        RejectOrderCommand rejectOrderCommand =
                new RejectOrderCommand(productReservationCancelledEvent.getOrderId(),
                        productReservationCancelledEvent.getReason());
        commandGateway.send(rejectOrderCommand);
    }


    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        LOGGER.info("Successfully rejected order with ID: " + orderRejectedEvent.getOrderId());
    }
}

@Component
class CompensationTransaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompensationTransaction.class);

    @Autowired
    private transient CommandGateway commandGateway;

    public void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .productId(productReservedEvent.getProductId())
                .quantity(productReservedEvent.getQuantity())
                .userId(productReservedEvent.getUserId())
                .reason(reason)
                .build();
        commandGateway.send(cancelProductReservationCommand);

        LOGGER.info("send cancelProductReservationCommand....");

    }

}