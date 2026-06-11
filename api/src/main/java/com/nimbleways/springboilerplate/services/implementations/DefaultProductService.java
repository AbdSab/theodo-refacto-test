package com.nimbleways.springboilerplate.services.implementations;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nimbleways.springboilerplate.constants.ErrorMessages;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.services.ProductService;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;

@Service
public class DefaultProductService implements ProductService {
    private final Map<ProductType, ProductProcessor> processors;

    public DefaultProductService(List<ProductProcessor> processors) {
        this.processors = processors.stream()
                .collect(Collectors.toMap(ProductProcessor::type, processor -> processor));
    }

    public void process(Product product) {
        ProductProcessor processor = processors.get(product.getType());
        if (processor == null) {
            throw new IllegalArgumentException(ErrorMessages.invalidProductProcessor(product.getType()));
        }
        processor.process(product);
    }
}