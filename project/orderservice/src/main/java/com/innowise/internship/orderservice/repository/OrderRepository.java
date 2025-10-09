package com.innowise.internship.orderservice.repository;

import com.innowise.internship.orderservice.model.Order;
import com.innowise.internship.orderservice.model.OrderStatus;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findOrdersByIdIn(List<Long> ids);

    List<Order> findOrdersByStatusIn(List<OrderStatus> statuses);
}
