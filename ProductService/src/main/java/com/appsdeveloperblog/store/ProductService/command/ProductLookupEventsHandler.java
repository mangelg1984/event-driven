package com.appsdeveloperblog.store.ProductService.command;

import com.appsdeveloperblog.store.ProductService.core.data.ProductLookupEntity;
import com.appsdeveloperblog.store.ProductService.core.data.ProductLookupRepository;
import com.appsdeveloperblog.store.ProductService.core.event.ProductCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {
        public static final Logger LOGGER = LoggerFactory.getLogger(ProductLookupEventsHandler.class);
        private final ProductLookupRepository productLookupRepository;

    public ProductLookupEventsHandler(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event){
         ProductLookupEntity productLookupEntity =
                 new ProductLookupEntity(event.getProductId(), event.getTitle());
         LOGGER.info("######### saving productLookupEntity: " + productLookupEntity);
         productLookupRepository.save(productLookupEntity);
    }
}
