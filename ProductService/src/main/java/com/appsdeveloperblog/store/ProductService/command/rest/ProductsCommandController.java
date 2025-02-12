package com.appsdeveloperblog.store.ProductService.command.rest;

import com.appsdeveloperblog.store.ProductService.command.CreateProductCommand;
import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductsCommandController {

    private final Environment env;
    private final CommandGateway commandGateway;
    public static final Logger LOGGER = LoggerFactory.getLogger(ProductsCommandController.class);

    public ProductsCommandController(Environment env, CommandGateway commandGateway){
        this.env = env;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductRestModel createProductRestModel){

        System.out.println("valor de createProductRestModel: " + createProductRestModel);

        CreateProductCommand createProductCommand = CreateProductCommand.builder().price(createProductRestModel.getPrice())
                .quantity(createProductRestModel.getQuantity())
                .title(createProductRestModel.getTitle())
                .productId(UUID.randomUUID().toString()).build();
        String returnValue;
        returnValue = commandGateway.sendAndWait(createProductCommand);

    /*
        try{
            LOGGER.info("before sendAndWait: ");
            returnValue = commandGateway.sendAndWait(createProductCommand);
            LOGGER.info("after sendAndWait: " + returnValue);
        }catch(Exception ex){
            returnValue = ex.getLocalizedMessage();
        }
    */
        return returnValue;
    }
}