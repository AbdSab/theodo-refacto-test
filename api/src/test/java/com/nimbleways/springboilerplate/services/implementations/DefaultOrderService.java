package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private DefaultOrderService defaultOrderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = mock(Order.class);
    }

    @Test
    void process_WhenOrderDoesNotExist_ShouldThrowEntityNotFoundException() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> defaultOrderService.process(orderId));

        verify(orderRepository, times(1)).findById(orderId);
        verifyNoInteractions(productService);
    }

    @Test
    void process_WhenOrderHasNoItems_ShouldReturnResponseWithoutProcessingProducts() {
        Long orderId = 1L;
        when(order.getId()).thenReturn(orderId);
        when(order.getItems()).thenReturn(Collections.emptySet());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ProcessOrderResponse response = defaultOrderService.process(orderId);

        assertEquals(orderId, response.id());
        verify(orderRepository, times(1)).findById(orderId);
        verifyNoInteractions(productService);
    }

    @Test
    void process_WhenOrderHasItems_ShouldProcessAllProductsAndReturnResponse() {
        Long orderId = 1L;
        Product product1 = mock(Product.class);
        Product product2 = mock(Product.class);
        Set<Product> items = Stream.of(product1, product2).collect(Collectors.toSet());

        when(order.getId()).thenReturn(orderId);
        when(order.getItems()).thenReturn(items);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        ProcessOrderResponse response = defaultOrderService.process(orderId);

        assertEquals(orderId, response.id());
        verify(orderRepository, times(1)).findById(orderId);
        verify(productService, times(1)).process(product1);
        verify(productService, times(1)).process(product2);
    }
}