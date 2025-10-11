package com.innowise.internship.orderservice.service.impl;

import com.innowise.internship.orderservice.dto.CreateItemDto;
import com.innowise.internship.orderservice.mapper.ItemMapper;
import com.innowise.internship.orderservice.model.Item;
import com.innowise.internship.orderservice.repository.ItemRepository;
import com.innowise.internship.orderservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public Item createItem(CreateItemDto createItemDto) {
        Item item = itemMapper.toEntity(createItemDto);
        return itemRepository.save(item);
    }
}

