package com.innowise.internship.orderservice.service.impl;

import com.innowise.internship.orderservice.client.UserServiceClient;
import com.innowise.internship.orderservice.dto.CreateOrderDto;
import com.innowise.internship.orderservice.dto.OrderItemDto;
import com.innowise.internship.orderservice.dto.OrderResponseDto;
import com.innowise.internship.orderservice.dto.UserResponseDto;
import com.innowise.internship.orderservice.exception.InvalidRequestParametersException;
import com.innowise.internship.orderservice.exception.ResourceNotFoundException;
import com.innowise.internship.orderservice.mapper.OrderMapper;
import com.innowise.internship.orderservice.model.Item;
import com.innowise.internship.orderservice.model.Order;
import com.innowise.internship.orderservice.model.OrderItem;
import com.innowise.internship.orderservice.model.OrderStatus;
import com.innowise.internship.orderservice.repository.ItemRepository;
import com.innowise.internship.orderservice.repository.OrderRepository;
import com.innowise.internship.orderservice.service.OrderService;
import com.innowise.internship.orderservice.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;
  private final OrderMapper orderMapper;
  private final UserServiceClient userServiceClient;
  private final KafkaProducerService kafkaProducerService;

  @Override
  @Transactional
  public OrderResponseDto createOrder(CreateOrderDto createOrderDto) {
    UserResponseDto user =
        userServiceClient.fetchUserByEmail(createOrderDto.getUserEmail(), getAuthToken());

    Order order = new Order();
    order.setUserId(user.getId());
    order.setStatus(OrderStatus.PENDING);

    List<OrderItem> orderItems =
        createOrderDto.getOrderItems().stream()
            .map(itemDto -> mapToOrderItem(itemDto, order))
            .toList();

    order.setOrderItems(orderItems);

    BigDecimal totalAmount =
        orderItems.stream()
            .map(
                orderItem ->
                    orderItem
                        .getItem()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    Order savedOrder = orderRepository.save(order);

    try {
      kafkaProducerService.sendOrderCreatedEvent(
          savedOrder.getId(), savedOrder.getUser_id(), totalAmount);
    } catch (Exception e) {
      throw new RuntimeException("Failed to send kafka message", e);
    }

    OrderResponseDto responseDto = orderMapper.toOrderResponseDto(savedOrder);
    responseDto.setUser(user);

    return responseDto;
  }

  @Override
  @Transactional(readOnly = true)
  public OrderResponseDto getOrderById(Long orderId) {
    Order order = findOrderOrThrow(orderId);

    return buildOrderResponseDto(order);
  }

  @Override
  public List<OrderResponseDto> findOrders(List<Long> ids, List<OrderStatus> statuses) {
    if (ids != null && !ids.isEmpty()) {
      return getOrdersByIds(ids);
    }
    if (statuses != null && !statuses.isEmpty()) {
      return getOrdersByStatuses(statuses);
    }
    throw new InvalidRequestParametersException("Provide list of ids or list of statuses");
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDto> getOrdersByIds(List<Long> ids) {
    return orderRepository.findOrdersByIdIn(ids).stream().map(this::buildOrderResponseDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDto> getOrdersByStatuses(List<OrderStatus> statuses) {
    return orderRepository.findOrdersByStatusIn(statuses).stream()
        .map(this::buildOrderResponseDto)
        .toList();
  }

  @Transactional
  @Override
  public OrderResponseDto updateOrderStatus(Long id, OrderStatus newStatus) {
    Order order = findOrderOrThrow(id);
    order.setStatus(newStatus);
    Order updatedOrder = orderRepository.save(order);

    return buildOrderResponseDto(updatedOrder);
  }

  @Transactional
  @Override
  public void deleteOrderById(Long id) {
    orderRepository.delete(findOrderOrThrow(id));
  }

  public Order findOrderOrThrow(Long id) {
    return orderRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Order with  id " + id + " not found"));
  }

  private OrderResponseDto buildOrderResponseDto(Order order) {
    UserResponseDto user = userServiceClient.fetchUserById(order.getUser_id(), getAuthToken());
    OrderResponseDto responseDto = orderMapper.toOrderResponseDto(order);

    List<OrderItemDto> orderItemDtos =
        order.getOrderItems().stream().map(this::mapOrderItemToDto).toList();

    responseDto.setOrderItems(orderItemDtos);
    responseDto.setUser(user);
    return responseDto;
  }

  private String getAuthToken() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return null;
    }
    final String authHeader = attributes.getRequest().getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }
    return authHeader.substring(7);
  }

  private OrderItem mapToOrderItem(OrderItemDto itemDto, Order order) {
    Item item =
        itemRepository
            .findById(itemDto.getItemId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Item with id " + itemDto.getItemId() + " not found"));

    OrderItem orderItem = new OrderItem();
    orderItem.setItem(item);
    orderItem.setQuantity(itemDto.getQuantity());
    orderItem.setOrder(order);

    return orderItem;
  }

  private OrderItemDto mapOrderItemToDto(OrderItem orderItem) {
    OrderItemDto orderItemDto = new OrderItemDto();

    Long itemId = (orderItem.getItem() != null) ? orderItem.getItem().getId() : null;

    orderItemDto.setItemId(itemId);
    orderItemDto.setQuantity(orderItem.getQuantity());

    return orderItemDto;
  }
}
