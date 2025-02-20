package com.appsdeveloperblog.store.ProductService.query;


import com.appsdeveloperblog.store.ProductService.core.data.ProductEntity;
import com.appsdeveloperblog.store.ProductService.core.data.ProductRepository;
import com.appsdeveloperblog.store.ProductService.core.event.ProductCreatedEvent;
import com.appsdeveloperblog.store.core.events.ProductReservedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProductEventHandler.class);

    private final ProductRepository productRepository;

    public ProductEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {
        LOGGER.info("######### trowing exception... ");
        throw exception;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);
        try {
            LOGGER.info("######### saving: " + productEntity);
            productRepository.save(productEntity);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void on(ProductReservedEvent event) throws Exception {
        ProductEntity productEntity = productRepository.findByProductId(event.getProductId());
        productEntity.setQuantity(productEntity.getQuantity() - event.getQuantity());
        productRepository.save(productEntity);
    }

}