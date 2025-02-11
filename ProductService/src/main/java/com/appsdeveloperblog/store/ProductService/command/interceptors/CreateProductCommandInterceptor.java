package com.appsdeveloperblog.store.ProductService.command.interceptors;

import com.appsdeveloperblog.store.ProductService.command.CreateProductCommand;
import com.appsdeveloperblog.store.ProductService.core.data.ProductLookupEntity;
import com.appsdeveloperblog.store.ProductService.core.data.ProductLookupRepository;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;


@Component
public class CreateProductCommandInterceptor  implements MessageDispatchInterceptor<CommandMessage<?>> {

    public static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    private final ProductLookupRepository productLookupRepository;

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {

        return (index, command) ->{

            LOGGER.info("Intercepted command: " + command.getPayload());

            if(CreateProductCommand.class.equals(command.getPayloadType())){
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                ProductLookupEntity productLookupEntity = this.productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());
                LOGGER.info("productLookupEntity found: " + command.getPayload());

                if(productLookupEntity != null){
                    throw new IllegalStateException(String.format("Product with productId %s or title %s already exist",
                            createProductCommand.getProductId(), createProductCommand.getTitle()));
                }
            }

            return command;
        };
    }
}