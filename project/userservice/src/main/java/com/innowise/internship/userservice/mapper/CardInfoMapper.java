package com.innowise.internship.userservice.mapper;

import com.innowise.internship.userservice.dto.AddCartRequestDto;
import com.innowise.internship.userservice.dto.CardInfoResponseDto;
import com.innowise.internship.userservice.model.CardInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    CardInfoResponseDto toDto (CardInfo cardInfo);

    CardInfo toEntity(AddCartRequestDto addCartRequestDto);
}
