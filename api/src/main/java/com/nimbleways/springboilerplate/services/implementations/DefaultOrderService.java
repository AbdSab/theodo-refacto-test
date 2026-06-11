package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.constants.ErrorMessages;
import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.OrderService;
import com.nimbleways.springboilerplate.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Override
    public ProcessOrderResponse process(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.orderNotFound(orderId)));
        order.getItems().forEach(productService::process);

        return new ProcessOrderResponse(order.getId());
    }
}
