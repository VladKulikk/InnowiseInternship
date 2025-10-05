package com.innowise.internship.userservice;

import com.innowise.internship.userservice.dto.AddCartRequestDto;
import com.innowise.internship.userservice.dto.CardInfoResponseDto;
import com.innowise.internship.userservice.dto.UserResponseDto;
import com.innowise.internship.userservice.exception.DuplicateResourceException;
import com.innowise.internship.userservice.exception.ResourceNotFoundException;
import com.innowise.internship.userservice.mapper.CardInfoMapper;
import com.innowise.internship.userservice.model.CardInfo;
import com.innowise.internship.userservice.model.User;
import com.innowise.internship.userservice.repository.CardInfoRepository;
import com.innowise.internship.userservice.service.CardInfoServiceImpl;
import com.innowise.internship.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardInfoServiceImplTest {

  @Mock private CardInfoRepository cardInfoRepository;

  @Mock private UserService userService;

  @Mock private CardInfoMapper cardInfoMapper;

  @InjectMocks private CardInfoServiceImpl cardInfoService;

  @Test
  public void getCardInfoById_whenCardExists_shouldReturnCardDto() {
    long cardId = 1L;
    CardInfo card = new CardInfo();
    CardInfoResponseDto expectedDto = new CardInfoResponseDto();

    when(cardInfoRepository.findById(cardId)).thenReturn(Optional.of(card));
    when(cardInfoMapper.toDto(card)).thenReturn(expectedDto);

    CardInfoResponseDto actualDto = cardInfoService.getCardInfoById(cardId);

    assertThat(actualDto).isNotNull();
    assertThat(actualDto).isEqualTo(expectedDto);
  }

  @Test
  public void getCardInfoById_whenCardDoesNotExist_shouldThrowException() {
    long cardId = 99L;
    when(cardInfoRepository.findById(cardId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> cardInfoService.getCardInfoById(cardId));
  }

  @Test
  public void addCardToUser_whenDataIsValid_shouldCreateAndReturnCardDto() {
    AddCartRequestDto requestDto = new AddCartRequestDto();
    requestDto.setUserId(1L);
    requestDto.setNumber("1234123412341234");

    User user = new User();
    CardInfo cardToSave = new CardInfo();
    CardInfo savedCard = new CardInfo();
    CardInfoResponseDto expectedDto = new CardInfoResponseDto();

    when(userService.findUserEntityById(1L)).thenReturn(user);
    when(cardInfoRepository.findByNumber(requestDto.getNumber())).thenReturn(Optional.empty());
    when(cardInfoMapper.toEntity(requestDto)).thenReturn(cardToSave);
    when(cardInfoRepository.save(cardToSave)).thenReturn(savedCard);
    when(cardInfoMapper.toDto(savedCard)).thenReturn(expectedDto);

    CardInfoResponseDto actualDto = cardInfoService.addCardToUser(requestDto);

    assertThat(actualDto).isEqualTo(expectedDto);
    verify(cardInfoRepository).save(cardToSave);
  }

  @Test
  public void addCardToUser_whenCardNumberExists_shouldThrowException() {
    AddCartRequestDto requestDto = new AddCartRequestDto();
    requestDto.setNumber("1234123412341234");

    when(cardInfoRepository.findByNumber(requestDto.getNumber()))
        .thenReturn(Optional.of(new CardInfo()));

    assertThrows(DuplicateResourceException.class, () -> cardInfoService.addCardToUser(requestDto));

    verify(userService, never()).findUserEntityById(any());
    verify(cardInfoRepository, never()).save(any());
  }

  @Test
  public void getCardsInfoByIds_whenIdsExists_shouldReturnCardDtoList() {
    List<Long> cardIds = List.of(1L, 2L);

    CardInfo card1 = new CardInfo();
    card1.setId(1L);
    CardInfo card2 = new CardInfo();
    card2.setId(2L);

    List<CardInfo> foundCards = List.of(card1, card2);

    CardInfoResponseDto expectedDto1 = new CardInfoResponseDto();
    expectedDto1.setId(1L);
    CardInfoResponseDto expectedDto2 = new CardInfoResponseDto();
    expectedDto2.setId(2L);

    when(cardInfoRepository.findAllByIdIn(cardIds)).thenReturn(foundCards);
    when(cardInfoMapper.toDto(card1)).thenReturn(expectedDto1);
    when(cardInfoMapper.toDto(card2)).thenReturn(expectedDto2);

    List<CardInfoResponseDto> actualResult = cardInfoService.getCardsInfoByIds(cardIds);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult).hasSize(2);
    assertThat(actualResult).containsExactlyInAnyOrder(expectedDto1, expectedDto2);
  }

  @Test
  public void getCardsInfoByUserId_whenUserExists_shouldReturnCardsDtoList() {
    long userId = 1L;
    List<CardInfo> cards = List.of(new CardInfo(), new CardInfo());

    when(userService.getUserById(userId)).thenReturn(new UserResponseDto());
    when(cardInfoRepository.findByUser_Id(userId)).thenReturn(cards);

    List<CardInfoResponseDto> actual = cardInfoService.getCardsInfoByUserId(userId);

    assertThat(actual).isNotNull();
    assertThat(actual.size()).isEqualTo(2);
    verify(cardInfoRepository).findByUser_Id(userId);
  }

  @Test
  public void getCardsInfoByUserId_whenUserNotFound_shouldThrowException() {
    long userId = 99L;
    when(userService.getUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

    assertThrows(
        ResourceNotFoundException.class, () -> cardInfoService.getCardsInfoByUserId(userId));
    verify(cardInfoRepository, never()).findByUser_Id(any());
  }

  @Test
  public void updateCardInfo_whenDataIsValid_shouldUpdateAndReturnCardDto() {
    long cardId = 1L;
    AddCartRequestDto requestDto = new AddCartRequestDto();
    requestDto.setHolder("New Holder");
    requestDto.setNumber("1234123412341234");

    CardInfo existingCard = new CardInfo();
    existingCard.setId(cardId);
    existingCard.setHolder("Old Holder");

    CardInfoResponseDto expectedDto = new CardInfoResponseDto();

    when(cardInfoRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
    when(cardInfoRepository.findByNumber(requestDto.getNumber())).thenReturn(Optional.empty());
    when(cardInfoRepository.save(any(CardInfo.class))).thenReturn(existingCard);
    when(cardInfoMapper.toDto(existingCard)).thenReturn(expectedDto);

    CardInfoResponseDto actualDto = cardInfoService.updateCardInfo(cardId, requestDto);

    assertThat(actualDto).isEqualTo(expectedDto);

    // ArgumentCaptor to verify the details of the saved object
    ArgumentCaptor<CardInfo> cardCaptor = ArgumentCaptor.forClass(CardInfo.class);
    verify(cardInfoRepository).save(cardCaptor.capture());

    assertThat(cardCaptor.getValue().getHolder()).isEqualTo("New Holder");
  }

  @Test
  public void deleteCard_whenCardExists_shouldDeleteCard() {
    long cardId = 1L;
    CardInfo cardToDelete = new CardInfo();
    cardToDelete.setId(cardId);

    when(cardInfoRepository.findById(cardId)).thenReturn(Optional.of(cardToDelete));

    cardInfoService.deleteCard(cardId);

    verify(cardInfoRepository, times(1)).delete(cardToDelete);
  }
}
