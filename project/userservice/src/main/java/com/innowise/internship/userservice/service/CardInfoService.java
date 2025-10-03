package com.innowise.internship.userservice.service;

import com.innowise.internship.userservice.dto.AddCartRequestDto;
import com.innowise.internship.userservice.dto.CardInfoResponseDto;

import java.util.List;

public interface CardInfoService {
  CardInfoResponseDto addCardToUser(AddCartRequestDto requestDto);

  CardInfoResponseDto getCardInfoById(Long id);

  List<CardInfoResponseDto> getCardsInfoByIds(List<Long> ids);

  List<CardInfoResponseDto> getCardsInfoByUserId(Long id);

  CardInfoResponseDto updateCardInfo(Long id, AddCartRequestDto requestDto);

  void deleteCard(Long id);
}
