package com.innowise.internship.userservice.service;

import com.innowise.internship.userservice.dto.AddCartRequestDto;
import com.innowise.internship.userservice.dto.CardInfoResponseDto;
import com.innowise.internship.userservice.mapper.CardInfoMapper;
import com.innowise.internship.userservice.model.CardInfo;
import com.innowise.internship.userservice.model.User;
import com.innowise.internship.userservice.repository.CardInfoRepository;
import com.innowise.internship.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;

    @Transactional
    @Override
    public CardInfoResponseDto addCartToUser(AddCartRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(() -> new RuntimeException("User with " + requestDto.getUserId() + " id is not found"));

        CardInfo newCard = cardInfoMapper.toEntity(requestDto);
        newCard.setUser(user);

        CardInfo savedCard = cardInfoRepository.save(newCard);

        return cardInfoMapper.toDto(savedCard);
    }

    @Transactional(readOnly = true)
    @Override
    public CardInfoResponseDto getCardInfoById(Long id) {
        return cardInfoMapper.toDto(findCardOrThrow(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardInfoResponseDto> getAllCardsInfo() {
        return cardInfoRepository.findAll().stream().map(cardInfoMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardInfoResponseDto> getAllCardsInfoByIds(List<Long> ids) {
        return cardInfoRepository.findAllByIdIn(ids).stream().map(cardInfoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<CardInfoResponseDto> getAllCardsInfoByUserId(Long userId) {
    if (!userRepository.existsById(userId)) {
        throw new RuntimeException("User with " + userId + " id is not found");
    }
      return cardInfoRepository.findById(userId).stream()
          .map(cardInfoMapper::toDto)
          .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CardInfoResponseDto updateCardInfo(Long id, AddCartRequestDto requestDto) {
        CardInfo existingCard = findCardOrThrow(id);

        existingCard.setNumber(requestDto.getNumber());
        existingCard.setHolder(requestDto.getHolder());
        existingCard.setExpirationDate(requestDto.getExpirationDate());

        CardInfo updatedCard = cardInfoRepository.save(existingCard);

        return cardInfoMapper.toDto(updatedCard);
    }

    @Transactional
    @Override
    public void deleteCard(Long id) {
        cardInfoRepository.delete(findCardOrThrow(id));
    }

    private CardInfo findCardOrThrow(Long id) {
        return cardInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("Card with " + id + " id is not found"));
    }
}
