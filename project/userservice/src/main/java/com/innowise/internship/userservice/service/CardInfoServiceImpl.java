package com.innowise.internship.userservice.service;

import com.innowise.internship.userservice.dto.AddCartRequestDto;
import com.innowise.internship.userservice.dto.CardInfoResponseDto;
import com.innowise.internship.userservice.exception.DuplicateResourceException;
import com.innowise.internship.userservice.exception.ResourceNotFoundException;
import com.innowise.internship.userservice.mapper.CardInfoMapper;
import com.innowise.internship.userservice.model.CardInfo;
import com.innowise.internship.userservice.model.User;
import com.innowise.internship.userservice.repository.CardInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {

  private final CardInfoRepository cardInfoRepository;
  private final UserService userService;
  private final CardInfoMapper cardInfoMapper;

  @Transactional
  // clears the old list of cards from cash when we added new card
  @CacheEvict(value = "cardsByUser", key = "#requestDto.userId")
  @Override
  public CardInfoResponseDto addCardToUser(AddCartRequestDto requestDto) {

    cardInfoRepository
        .findByNumber(requestDto.getNumber())
        .ifPresent(
            card -> {
              throw new DuplicateResourceException("Card number not found");
            });

    User user = userService.findUserEntityById(requestDto.getUserId());

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
  public List<CardInfoResponseDto> getCardsInfoByIds(List<Long> ids) {
    return cardInfoRepository.findAllByIdIn(ids).stream()
        .map(cardInfoMapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "cardsByUser", key = "#userId")
  @Override
  public List<CardInfoResponseDto> getCardsInfoByUserId(Long userId) {

    // if user does not exist, then throws ResourceNotFoundException
    userService.getUserById(userId);

    return cardInfoRepository.findByUser_Id(userId).stream()
        .map(cardInfoMapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional
  @CacheEvict(value = "cardsByUser", key = "#root.target.findCardOrThrow(#id).getUser().getId()")
  @Override
  public CardInfoResponseDto updateCardInfo(Long id, AddCartRequestDto requestDto) {
    CardInfo existingCard = findCardOrThrow(id);

    cardInfoRepository
        .findByNumber(requestDto.getNumber())
        .ifPresent(
            card -> {
              if (!card.getId().equals(id)) {
                throw new DuplicateResourceException(
                    "Card with number " + requestDto.getNumber() + " already exists");
              }
            });

    existingCard.setNumber(requestDto.getNumber());
    existingCard.setHolder(requestDto.getHolder());
    existingCard.setExpirationDate(requestDto.getExpirationDate());

    CardInfo updatedCard = cardInfoRepository.save(existingCard);

    return cardInfoMapper.toDto(updatedCard);
  }

  @Transactional
  @CacheEvict(
      value = "cardsByUser",
      key = "#root.target.findCardOrThrow(#id).getUser().getId()",
      beforeInvocation = true)
  @Override
  public void deleteCard(Long id) {
    cardInfoRepository.delete(findCardOrThrow(id));
  }

  public CardInfo findCardOrThrow(Long id) {
    return cardInfoRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Card with " + id + " id is not found"));
  }
}
