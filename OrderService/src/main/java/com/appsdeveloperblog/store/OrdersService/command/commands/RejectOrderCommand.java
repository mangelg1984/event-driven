package com.appsdeveloperblog.store.OrdersService.command.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class RejectOrderCommand {
    @TargetAggregateIdentifier
    public final String orderId;
    private final String reason;
}
