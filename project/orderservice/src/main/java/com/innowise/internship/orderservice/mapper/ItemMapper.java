package com.innowise.internship.orderservice.mapper;

import com.innowise.internship.orderservice.dto.CreateItemDto;
import com.innowise.internship.orderservice.model.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toEntity(CreateItemDto createItemDto);
}
