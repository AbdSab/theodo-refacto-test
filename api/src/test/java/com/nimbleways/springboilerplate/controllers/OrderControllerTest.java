package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.constants.ErrorMessages;
import com.nimbleways.springboilerplate.contollers.OrderController;
import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.exceptions.GlobalExceptionHandler;
import com.nimbleways.springboilerplate.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {OrderController.class, GlobalExceptionHandler.class})
class OrderControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderService orderService;

        @Test
        void processOrder_ShouldReturnOk_WhenOrderExists() throws Exception {
                Long orderId = 1L;
                ProcessOrderResponse mockResponse = new ProcessOrderResponse(1L);

                when(orderService.process(orderId)).thenReturn(mockResponse);

                mockMvc.perform(post("/orders/{orderId}/processOrder", orderId)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        void processOrder_ShouldReturnNotFound_WhenEntityNotFoundExceptionThrown() throws Exception {
                Long orderId = 99L;
                String errorMessage = ErrorMessages.orderNotFound(99L);

                when(orderService.process(orderId))
                        .thenThrow(new EntityNotFoundException(errorMessage));

                mockMvc.perform(post("/orders/{orderId}/processOrder", orderId)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message").value(errorMessage));
        }

        @Test
        void processOrder_ShouldReturnInternalServerError_WhenGenericExceptionThrown() throws Exception {
                Long orderId = 1L;

                when(orderService.process(anyLong()))
                        .thenThrow(new RuntimeException(ErrorMessages.INTERNAL_SERVER_ERROR));

                mockMvc.perform(post("/orders/{orderId}/processOrder", orderId)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.message").exists());
        }
}