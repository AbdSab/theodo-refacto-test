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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NormalProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NormalProductProcessor normalProductProcessor;

    private Product product;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
    }

    @Test
    void type_ShouldReturnNormal() {
        assertEquals(ProductType.NORMAL, normalProductProcessor.type());
    }

    @Test
    void process_WhenProductHasStock_ShouldDecrementStockAndSave() {
        when(product.hasStock()).thenReturn(true);

        normalProductProcessor.process(product);

        verify(product, times(1)).decrementStock();
        verify(productRepository, times(1)).save(product);
        verifyNoInteractions(notificationService); // Notification should NOT trigger
    }

    @Test
    void process_WhenProductHasNoStockAndPositiveLeadTime_ShouldSendDelayNotification() {
        when(product.hasStock()).thenReturn(false);
        when(product.getLeadTime()).thenReturn(5);
        when(product.getName()).thenReturn("Wireless Mouse");

        normalProductProcessor.process(product);

        verify(product, never()).decrementStock();
        verifyNoInteractions(productRepository);
        verify(notificationService, times(1)).sendDelayNotification(5, "Wireless Mouse");
    }

    @Test
    void process_WhenProductHasNoStockAndZeroLeadTime_ShouldDoNothing() {
        when(product.hasStock()).thenReturn(false);
        when(product.getLeadTime()).thenReturn(0);

        normalProductProcessor.process(product);

        verify(product, never()).decrementStock();
        verifyNoInteractions(productRepository);
        verifyNoInteractions(notificationService);
    }
}