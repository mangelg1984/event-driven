package com.appsdeveloperblog.store.ProductService.command.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRestModel {
    @NotBlank(message = "Product title is a required field")
    private String title;

    @Min(message = "Price cannot be lower than 1", value = 1)
    private BigDecimal price;

    @Min(message = "Quantity cannot be lower than 1", value = 1)
    @Max(message = "Quantity cannot be larger than 5", value = 5)
    private Integer quantity;
}
