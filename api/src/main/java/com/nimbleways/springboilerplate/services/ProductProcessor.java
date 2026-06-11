package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;

public interface ProductProcessor {
    ProductType type();
    void process(Product product);
}
