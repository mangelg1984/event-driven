package com.appsdeveloperblog.store.core.events;

import lombok.Value;

@Value
public class PaymentProcessedEvent {
    private final String orderId;
    private final String paymentId;
}