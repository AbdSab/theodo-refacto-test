package com.nimbleways.springboilerplate.constants;

import com.nimbleways.springboilerplate.enums.ProductType;

public final class ErrorMessages {

    private ErrorMessages() {}

    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";

    public static String invalidProductProcessor(ProductType productType) {
        return String.format("Processor of type '%s' not found", productType);
    }

    public static String productOutOfStock(Long id) {
        return String.format("Sorry, Product with id '%s' is currently out of stock.", id);
    }

    public static String orderNotFound(Long orderId) {
        return String.format("Order with id %d not found", orderId);
    }
}