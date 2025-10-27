package com.innowise.internship.orderservice.mapper;

import com.innowise.internship.orderservice.dto.OrderResponseDto;
import com.innowise.internship.orderservice.model.Order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "user", ignore = true)
    OrderResponseDto toOrderResponseDto(Order order);
}
