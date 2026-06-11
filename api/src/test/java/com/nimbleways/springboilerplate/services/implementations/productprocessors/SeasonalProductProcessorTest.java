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
class SeasonalProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SeasonalProductProcessor seasonalProductProcessor;

    private Product product;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
    }

    @Test
    void type_ShouldReturnSeasonal() {
        assertEquals(ProductType.SEASONAL, seasonalProductProcessor.type());
    }

    @Test
    void process_WhenInSeasonAndHasStock_ShouldDecrementStockAndSave() {
        when(product.isInSeason()).thenReturn(true);
        when(product.hasStock()).thenReturn(true);

        seasonalProductProcessor.process(product);

        verify(product, times(1)).decrementStock();
        verify(productRepository, times(1)).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void process_WhenLeadTimeExceedsSeason_ShouldNotifyMakeUnavailableAndSave() {
        when(product.isInSeason()).thenReturn(false);
        when(product.willSeasonalLeadTimeExceedSeason()).thenReturn(true);
        when(product.getName()).thenReturn("Winter Coat");

        seasonalProductProcessor.process(product);

        verify(product, never()).decrementStock();
        verify(notificationService, times(1)).sendOutOfStockNotification("Winter Coat");
        verify(product, times(1)).setAvailable(0);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void process_WhenLeadTimeDoesNotExceedAndSeasonNotStarted_ShouldSendOutOfStockAndSave() {
        when(product.isInSeason()).thenReturn(true);
        when(product.hasStock()).thenReturn(false);
        when(product.willSeasonalLeadTimeExceedSeason()).thenReturn(false);
        when(product.hasSeasonStarted()).thenReturn(false);
        when(product.getName()).thenReturn("Summer Fan");

        seasonalProductProcessor.process(product);

        verify(product, never()).decrementStock();
        verify(notificationService, times(1)).sendOutOfStockNotification("Summer Fan");
        verify(product, never()).setAvailable(0);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void process_WhenLeadTimeDoesNotExceedAndSeasonStarted_ShouldSendDelayAndSave() {
        when(product.isInSeason()).thenReturn(true);
        when(product.hasStock()).thenReturn(false);
        when(product.willSeasonalLeadTimeExceedSeason()).thenReturn(false);
        when(product.hasSeasonStarted()).thenReturn(true);
        when(product.getLeadTime()).thenReturn(7);
        when(product.getName()).thenReturn("Autumn Boots");

        seasonalProductProcessor.process(product);

        verify(product, never()).decrementStock();
        verify(notificationService, times(1)).sendDelayNotification(7, "Autumn Boots");
        verify(product, never()).setAvailable(0);
        verify(productRepository, times(1)).save(product);
    }
}
