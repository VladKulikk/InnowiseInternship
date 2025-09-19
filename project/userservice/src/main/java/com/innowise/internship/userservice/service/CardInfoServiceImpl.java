package com.innowise.internship.userservice.service;

import com.innowise.internship.userservice.dto.AddCartRequestDto;
import com.innowise.internship.userservice.dto.CardInfoResponseDto;
import com.innowise.internship.userservice.exception.DuplicateResourseException;
import com.innowise.internship.userservice.exception.ResourceNotFoundException;
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

        cardInfoRepository.findByNumber(requestDto.getNumber()).ifPresent(card -> {
            throw new DuplicateResourseException("Card number not found");
        });

        User user = userRepository
            .findById(requestDto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User with " + requestDto.getUserId() + " id is not found"));

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
    public List<CardInfoResponseDto> getCardsInfoByIds(List<Long> ids) {
        return cardInfoRepository.findAllByIdIn(ids).stream().map(cardInfoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<CardInfoResponseDto> getCardsInfoByUserId(Long userId) {

    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User with " + userId + " id is not found");
    }

      return cardInfoRepository.findByUser_Id(userId).stream()
          .map(cardInfoMapper::toDto)
          .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CardInfoResponseDto updateCardInfo(Long id, AddCartRequestDto requestDto) {
        CardInfo existingCard = findCardOrThrow(id);

        cardInfoRepository.findByNumber(requestDto.getNumber()).ifPresent(card -> {
            if(!card.getId().equals(id)){
                throw new DuplicateResourseException("Card with number " + requestDto.getNumber() + " already exists");
            }
        });

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
    return cardInfoRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Card with " + id + " id is not found"));
    }
}
