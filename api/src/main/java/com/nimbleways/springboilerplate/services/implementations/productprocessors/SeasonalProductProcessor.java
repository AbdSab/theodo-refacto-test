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
public class SeasonalProductProcessor implements ProductProcessor {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public ProductType type() {
        return ProductType.SEASONAL;
    }

    @Override
    public void process(Product product) {
        if (product.isInSeason() && product.hasStock()) {
            product.decrementStock();
            productRepository.save(product);
            return;
        }

        if (product.willSeasonalLeadTimeExceedSeason()) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
        } else if (!product.hasSeasonStarted()) {
            notificationService.sendOutOfStockNotification(product.getName());
        } else {
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }

        productRepository.save(product);
    }
}
