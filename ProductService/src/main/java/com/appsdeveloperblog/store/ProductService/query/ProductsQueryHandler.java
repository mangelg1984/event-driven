package com.appsdeveloperblog.store.ProductService.query;

import com.appsdeveloperblog.store.ProductService.core.data.ProductEntity;
import com.appsdeveloperblog.store.ProductService.core.data.ProductRepository;
import com.appsdeveloperblog.store.ProductService.query.rest.ProductRestModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductsQueryHandler {
        private final ProductRepository productRepository;
        public ProductsQueryHandler(ProductRepository productRepository){
            this.productRepository = productRepository;
        }

        @QueryHandler
        public List<ProductRestModel> findProducts(FindProductsQuery query){
            List<ProductRestModel> productRestModels = new ArrayList<>();

            List<ProductEntity> storedProducts = productRepository.findAll();

            storedProducts.forEach(sp -> {
                ProductRestModel productRestModel = new ProductRestModel();
                BeanUtils.copyProperties(sp,productRestModel);
                productRestModels.add(productRestModel);
            });

            return productRestModels;
        }
}
