package com.appsdeveloperblog.store.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.appsdeveloperblog.store.core.model.PaymentDetails;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProcessPaymentCommand {

	@TargetAggregateIdentifier
	private final String paymentId;
	private final String orderId;
	private final PaymentDetails paymentDetails;

}