package com.innowise.internship.orderservice.mapper;
import com.innowise.internship.orderservice.dto.OrderResponseDto;
import com.innowise.internship.orderservice.model.Order;

import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponseDto toOrderResponseDto(Order order);
}
