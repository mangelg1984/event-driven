package com.appsdeveloperblog.store.ProductService.command.rest;

import com.appsdeveloperblog.store.ProductService.command.CreateProductCommand;
import com.appsdeveloperblog.store.ProductService.core.event.ProductCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate
public class ProductAgregate {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProductAgregate.class);

    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    public ProductAgregate(){
    }
    @CommandHandler
    public ProductAgregate(CreateProductCommand  createProductCommand){
        if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Price Cannot be less or equal than zero");
        }
        if(createProductCommand.getTitle() == null
            || createProductCommand.getTitle().isBlank()){
            throw new IllegalArgumentException("Title Cannot be empty");
        }

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);
        LOGGER.info("--------- before apply: ");
        AggregateLifecycle.apply(productCreatedEvent);
        LOGGER.info("--------- after apply: ");
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent){
        this.productId = productCreatedEvent.getProductId();
        this.price = productCreatedEvent.getPrice();
        this.productId = productCreatedEvent.getProductId();
        this.quantity = productCreatedEvent.getQuantity();
    }
}
