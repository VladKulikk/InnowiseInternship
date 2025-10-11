package com.innowise.internship.orderservice.service.impl;

import com.innowise.internship.orderservice.client.UserServiceClient;
import com.innowise.internship.orderservice.dto.CreateOrderDto;
import com.innowise.internship.orderservice.dto.OrderResponseDto;
import com.innowise.internship.orderservice.dto.UserResponseDto;
import com.innowise.internship.orderservice.exception.ResourceNotFoundException;
import com.innowise.internship.orderservice.mapper.OrderMapper;
import com.innowise.internship.orderservice.model.Item;
import com.innowise.internship.orderservice.model.Order;
import com.innowise.internship.orderservice.model.OrderItem;
import com.innowise.internship.orderservice.model.OrderStatus;
import com.innowise.internship.orderservice.repository.ItemRepository;
import com.innowise.internship.orderservice.repository.OrderRepository;
import com.innowise.internship.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public OrderResponseDto createOrder(CreateOrderDto createOrderDto) {
        UserResponseDto user = userServiceClient.fetchUserByEmail(createOrderDto.getUserEmail(), getAuthToken());

        Order order = new Order();
        order.setUser_id(user.getId());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = createOrderDto.getOrderItems().stream()
                .map(itemDto -> {
                    Item item = itemRepository.findById(itemDto.getItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Item with id " + itemDto.getItemId() + " not found"));

                    OrderItem orderItem = new OrderItem();
                    orderItem.setItem(item);
                    orderItem.setQuantity(itemDto.getQuantity());
                    orderItem.setOrder(order);
                    return orderItem;
                }).toList();

        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        OrderResponseDto responseDto = orderMapper.toOrderResponseDto(savedOrder);
        responseDto.setUser(user);

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = findOrderOrThrow(orderId);

        return buildCombinedResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByIds(List<Long> ids) {
        return orderRepository.findOrdersByIdIn(ids).stream()
                .map(this::buildCombinedResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStatuses(List<OrderStatus> statuses) {
        return orderRepository.findOrdersByStatusIn(statuses).stream()
                .map(this::buildCombinedResponseDto)
                .toList();
    }

    @Transactional
    @Override
    public OrderResponseDto updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = findOrderOrThrow(id);
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return buildCombinedResponseDto(updatedOrder);
    }

    @Transactional
    @Override
    public void deleteOrderById(Long id) {
        orderRepository.delete(findOrderOrThrow(id));
    }

    public Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with  id " + id + " not found"));
    }

    private OrderResponseDto buildCombinedResponseDto(Order order) {
        UserResponseDto user = userServiceClient.fetchUserById(order.getUser_id(), getAuthToken());
        OrderResponseDto responseDto = orderMapper.toOrderResponseDto(order);
        responseDto.setUser(user);
        return responseDto;
    }

    private String getAuthToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        final String authHeader = attributes.getRequest().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
