package com.innowise.internship.orderservice;

import com.innowise.internship.orderservice.client.UserServiceClient;
import com.innowise.internship.orderservice.dto.CreateOrderDto;
import com.innowise.internship.orderservice.dto.OrderItemDto;
import com.innowise.internship.orderservice.dto.OrderResponseDto;
import com.innowise.internship.orderservice.dto.UserResponseDto;
import com.innowise.internship.orderservice.exception.ResourceNotFoundException;
import com.innowise.internship.orderservice.mapper.OrderMapper;
import com.innowise.internship.orderservice.model.Item;
import com.innowise.internship.orderservice.model.Order;
import com.innowise.internship.orderservice.model.OrderStatus;
import com.innowise.internship.orderservice.repository.ItemRepository;
import com.innowise.internship.orderservice.repository.OrderRepository;
import com.innowise.internship.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    public void createOrder_whenDataIsValid_shouldCreateAndReturnOrder() {
        mockRequestContext();

        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setUserEmail("test@example.com");

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setItemId(1L);
        itemDto.setQuantity(2);

        createOrderDto.setOrderItems(List.of(itemDto));

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(123L);

        Item item = new Item();

        Order savedOrder = new Order();

        OrderResponseDto expectedOrderResponseDto = new OrderResponseDto();

        when(userServiceClient.fetchUserByEmail(eq("test@example.com"), anyString())).thenReturn(userResponseDto);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toOrderResponseDto(savedOrder)).thenReturn(expectedOrderResponseDto);

        OrderResponseDto actualOrderResponse = orderService.createOrder(createOrderDto);

        assertThat(actualOrderResponse).isNotNull();
        assertThat(actualOrderResponse.getUser()).isEqualTo(userResponseDto);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    public void createOrder_ItemNotFound_shouldThrowException() {
        mockRequestContext();

        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setUserEmail("test@example.com");

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setItemId(99L);
        itemDto.setQuantity(1);
        createOrderDto.setOrderItems(List.of(itemDto));

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(123L);

        when(userServiceClient.fetchUserByEmail(eq("test@example.com"), anyString())).thenReturn(userResponseDto);
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(createOrderDto));

        verify(orderRepository, never()).save(any());
    }

    @Test
    public void getOrderById_whenOrderExists_shouldReturnOrderResponseDto() {
        mockRequestContext();

        long orderId = 1L;

        Order order = new Order();
        order.setUser_id(123L);

        UserResponseDto userResponseDto = new UserResponseDto();

        OrderResponseDto expectedOrderResponseDto = new OrderResponseDto();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userServiceClient.fetchUserById(eq(123L), anyString())).thenReturn(new UserResponseDto());
        when(orderMapper.toOrderResponseDto(order)).thenReturn(expectedOrderResponseDto);

        OrderResponseDto result = orderService.getOrderById(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(userResponseDto);
    }

    @Test
    public void getOrderById_whenOrderNotFound_shouldThrowException() {
        long orderId = 99L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    public void getOrdersByIds_whenOrderExists_shouldReturnOrderResponseDtoList() {
        mockRequestContext();

        List<Long> ids = List.of(1L, 2L);

        Order order1 = new Order();
        order1.setUser_id(123L);
        Order order2 = new Order();
        order2.setUser_id(124L);
        List<Order> orders = List.of(order1, order2);

        when(orderRepository.findOrdersByIdIn(ids)).thenReturn(orders);
        when(userServiceClient.fetchUserById(eq(123L), anyString())).thenReturn(new UserResponseDto());
        when(orderMapper.toOrderResponseDto(any(Order.class))).thenReturn(new OrderResponseDto());

        List<OrderResponseDto> result = orderService.getOrdersByIds(ids);

        assertThat(result).hasSize(2);
    }

    @Test
    public void getOrdersByIds_whenNoOrdersFound_shouldReturnEmptyList() {
        List<Long> ids = List.of(99L);

        when(orderRepository.findOrdersByIdIn(ids)).thenReturn(Collections.emptyList());

        List<OrderResponseDto> result = orderService.getOrdersByIds(ids);

        assertThat(result).isEmpty();
    }

    @Test
    public void getOrdersByStatuses_whenOrdersExists_shouldReturnOrderResponseDtoList() {
        List<OrderStatus> statuses = List.of(OrderStatus.PENDING);

        Order order1 = new Order();
        order1.setUser_id(123L);
        List<Order> orders = List.of(order1);

        when(orderRepository.findOrdersByStatusIn(statuses)).thenReturn(orders);
        when(userServiceClient.fetchUserById(eq(123L), anyString())).thenReturn(new UserResponseDto());
        when(orderMapper.toOrderResponseDto(any(Order.class))).thenReturn(new OrderResponseDto());

        List<OrderResponseDto> result = orderService.getOrdersByStatuses(statuses);

        assertThat(result).hasSize(1);
    }

    @Test
    public void updateOrderStatus_whenOrderExists_shouldUpdateAndReturnOrderResponseDto() {
        long orderId = 1L;
        Order order = new Order();
        order.setUser_id(123L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(userServiceClient.fetchUserById(eq(123L), anyString())).thenReturn(new UserResponseDto());
        when(orderMapper.toOrderResponseDto(any(Order.class))).thenReturn(new OrderResponseDto());

        orderService.updateOrderStatus(orderId, OrderStatus.SHIPPED);

        verify(orderRepository).save(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    public void updateOrderStatus_whenOrderNotFound_shouldThrowException() {
        long orderId = 99L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderStatus(orderId, OrderStatus.SHIPPED));
    }

    @Test
    public void deleteOrderById_whenOrderExists_shouldCallDelete(){
        long orderId = 1L;
        Order order = new Order();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.deleteOrderById(orderId);

        verify(orderRepository).delete(order);
    }

    @Test
    public void deleteOrderById_whenOrderNotFound_shouldThrowException() {
        long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrderById(orderId));
    }

    private void mockRequestContext(){
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer fake-jwt-token");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
    }
}
