package com.innowise.internship.orderservice.service;

import com.innowise.internship.orderservice.dto.CreateOrderDto;
import com.innowise.internship.orderservice.dto.OrderResponseDto;
import com.innowise.internship.orderservice.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(CreateOrderDto createOrderDto);
    OrderResponseDto getOrderById(Long orderId);
    List<OrderResponseDto> getOrdersByIds(List<Long> ids);
    List<OrderResponseDto> getOrdersByStatuses(List<OrderStatus> statuses);
    OrderResponseDto updateOrderStatus(Long id, OrderStatus newStatus);
    void deleteOrderById(Long id);
    List<OrderResponseDto> findOrders(List<Long> ids, List<OrderStatus> statuses);
}
