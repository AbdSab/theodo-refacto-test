package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultProductServiceTest {

    @Mock
    private ProductProcessor normalProcessor;

    @Mock
    private ProductProcessor expirableProcessor;

    @Mock
    private Product product;

    private DefaultProductService defaultProductService;

    @BeforeEach
    void setUp() {
        when(normalProcessor.type()).thenReturn(ProductType.NORMAL);
        when(expirableProcessor.type()).thenReturn(ProductType.EXPIRABLE);

        List<ProductProcessor> processors = Arrays.asList(normalProcessor, expirableProcessor);
        defaultProductService = new DefaultProductService(processors);
    }

    @Test
    void process_WhenProcessorExists_ShouldInvokeProcessor() {
        when(product.getType()).thenReturn(ProductType.NORMAL);

        defaultProductService.process(product);

        verify(normalProcessor, times(1)).process(product);
        verify(expirableProcessor, never()).process(any());
    }

    @Test
    void process_WhenProcessorDoesNotExist_ShouldThrowIllegalArgumentException() {
        when(product.getType()).thenReturn(ProductType.SEASONAL);

        assertThrows(IllegalArgumentException.class, () -> defaultProductService.process(product));

        verify(normalProcessor, never()).process(any());
        verify(expirableProcessor, never()).process(any());
    }
}
