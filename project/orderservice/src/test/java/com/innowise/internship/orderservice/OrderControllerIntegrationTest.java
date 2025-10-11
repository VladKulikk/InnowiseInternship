package com.innowise.internship.orderservice;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.innowise.internship.orderservice.dto.CreateOrderDto;
import com.innowise.internship.orderservice.dto.OrderItemDto;
import com.innowise.internship.orderservice.model.Item;
import com.innowise.internship.orderservice.model.Order;
import com.innowise.internship.orderservice.model.OrderStatus;
import com.innowise.internship.orderservice.repository.ItemRepository;
import com.innowise.internship.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

public class OrderControllerIntegrationTest extends AbstractIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        wireMockServer.resetAll();
    }

    @Test
    public void createOrder_whenDataIsValid_shouldCreateAndReturn201() throws Exception {
        Item savedItem = itemRepository.save(initItem("Test item", "9.99"));

        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/api/v1/users/by-email"))
                .withQueryParam("email", equalTo("test@example.com"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 123, \"name\": \"Test\", \"email\": \"test@example.com\"}")
                        .withStatus(200)));

        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setUserEmail("test@example.com");

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setItemId(savedItem.getId());
        orderItemDto.setQuantity(2);

        createOrderDto.setOrderItems(List.of(orderItemDto));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.user.id").value(123));
    }

    @Test
    public void createOrder_whenUserNotFound_shouldReturn404() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/api/v1/users/by-email"))
                .withQueryParam("email", equalTo("notfound@example.com"))
                .willReturn(aResponse().withStatus(404)));

        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setUserEmail("notfound@example.com");

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setItemId(99L);
        itemDto.setQuantity(1);
        createOrderDto.setOrderItems(List.of(itemDto));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOrderById_whenOrderExists_shouldReturnOrder() throws Exception {
        Order savedOrder = orderRepository.save(initOrder(1L, OrderStatus.PENDING));
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/api/v1/users/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1}")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/{id}", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()));
    }

    @Test
    public void getOrderById_whenOrderNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOrders_whenNoParams_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders")).andExpect(status().isBadRequest());
    }

    @Test
    public void updateOrderStatus_whenOrderExists_shouldUpdateStatus() throws Exception {
        Order savedOrder = orderRepository.save(initOrder(1L, OrderStatus.PENDING));

        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/api/v1/users/1"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("{\"id\": 1}")));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orders/{id}/status", savedOrder.getId())
                    .param("newStatus", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    public void updateOrderStatus_whenOrderNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orders/{id}/status", 999L)
                    .param("newStatus", "SHIPPED"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteOrderById_whenOrderExists_shouldDeleteOrderAndReturn204() throws Exception {
        Order savedOrder = orderRepository.save(initOrder(1L, OrderStatus.PENDING));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orders/{id}", savedOrder.getId()))
                .andExpect(status().isNoContent());

        assertThat(orderRepository.findById(savedOrder.getId())).isEmpty();
    }

    @Test
    public void deleteOrderById_whenOrderNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orders/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    private Item initItem(String name, String price){
        Item item = new Item();
        item.setName(name);
        item.setPrice(new BigDecimal(price));
        return item;
    }

    private Order initOrder(Long userId, OrderStatus status){
        Order order = new Order();
        order.setUser_id(userId);
        order.setStatus(status);
        return order;
    }
}
