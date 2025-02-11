package com.appsdeveloperblog.store.ProductService.query;

import com.appsdeveloperblog.store.ProductService.command.interceptors.CreateProductCommandInterceptor;
import com.appsdeveloperblog.store.ProductService.core.data.ProductEntity;
import com.appsdeveloperblog.store.ProductService.core.data.ProductRepository;
import com.appsdeveloperblog.store.ProductService.core.event.ProductCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProductEventHandler.class);

    private final ProductRepository productRepository;

    public ProductEventHandler(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    @EventHandler
    public void on(ProductCreatedEvent event){
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);
        LOGGER.info("######### saving: " + productEntity);
        productRepository.save(productEntity);
    }
}