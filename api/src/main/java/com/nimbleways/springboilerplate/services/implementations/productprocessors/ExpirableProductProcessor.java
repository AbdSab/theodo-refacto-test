package com.nimbleways.springboilerplate.services.implementations.productprocessors;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpirableProductProcessor implements ProductProcessor {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public ProductType type() {
        return ProductType.EXPIRABLE;
    }

    @Override
    public void process(Product product) {
        if (product.hasStock() && !product.isExpired()) {
            product.decrementStock();
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
        }
        productRepository.save(product);
    }
}
