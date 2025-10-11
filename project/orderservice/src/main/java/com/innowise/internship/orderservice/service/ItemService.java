package com.innowise.internship.orderservice.service;

import com.innowise.internship.orderservice.dto.CreateItemDto;
import com.innowise.internship.orderservice.model.Item;

public interface ItemService {
    Item createItem(CreateItemDto createItemDto);
}
