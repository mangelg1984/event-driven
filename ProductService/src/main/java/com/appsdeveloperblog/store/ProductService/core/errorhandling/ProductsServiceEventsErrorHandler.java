package com.appsdeveloperblog.store.ProductService.core.errorhandling;

import com.appsdeveloperblog.store.ProductService.command.rest.ProductAgregate;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductsServiceEventsErrorHandler implements ListenerInvocationErrorHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProductsServiceEventsErrorHandler.class);

    @Override
    public void onError(@NotNull Exception exception, @NotNull EventMessage<?> event, @NotNull EventMessageHandler eventHandler) throws Exception {
        LOGGER.info("######### onError ProductsServiceEventsErrorHandler");
        throw exception;
    }
}
