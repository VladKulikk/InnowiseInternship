package com.innowise.internship.orderservice.repository;

import com.innowise.internship.orderservice.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {}
