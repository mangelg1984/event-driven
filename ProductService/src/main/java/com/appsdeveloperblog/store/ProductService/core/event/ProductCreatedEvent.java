package com.appsdeveloperblog.store.ProductService.core.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreatedEvent {
    private  String productId;

    private  String title;
    private  BigDecimal price;
    private  Integer quantity;
}