package com.nimbleways.springboilerplate.services.implementations.productprocessors;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpirableProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirableProductProcessor expirableProductProcessor;

    private Product product;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
    }

    @Test
    void type_ShouldReturnExpirable() {
        assertEquals(ProductType.EXPIRABLE, expirableProductProcessor.type());
    }

    @Test
    void process_WhenProductHasStockAndNotExpired_ShouldDecrementStockAndSave() {
        when(product.hasStock()).thenReturn(true);
        when(product.isExpired()).thenReturn(false);

        expirableProductProcessor.process(product);

        verify(product, times(1)).decrementStock();
        verifyNoInteractions(notificationService);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void process_WhenProductHasNoStock_ShouldNotifyMakeUnavailableAndSave() {
        LocalDate expiryDate = LocalDate.now().plusDays(10);
        when(product.hasStock()).thenReturn(false);
        when(product.getName()).thenReturn("Milk");
        when(product.getExpiryDate()).thenReturn(expiryDate);

        expirableProductProcessor.process(product);

        verify(product, never()).decrementStock();
        verify(notificationService, times(1)).sendExpirationNotification("Milk", expiryDate);
        verify(product, times(1)).setAvailable(0);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void process_WhenProductIsExpired_ShouldNotifyMakeUnavailableAndSave() {
        LocalDate expiryDate = LocalDate.now().minusDays(2);
        when(product.hasStock()).thenReturn(true);
        when(product.isExpired()).thenReturn(true);
        when(product.getName()).thenReturn("Yogurt");
        when(product.getExpiryDate()).thenReturn(expiryDate);

        expirableProductProcessor.process(product);

        verify(product, never()).decrementStock();
        verify(notificationService, times(1)).sendExpirationNotification("Yogurt", expiryDate);
        verify(product, times(1)).setAvailable(0);
        verify(productRepository, times(1)).save(product);
    }
}