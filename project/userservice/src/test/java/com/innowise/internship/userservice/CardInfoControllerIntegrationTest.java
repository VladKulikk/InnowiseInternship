package com.innowise.internship.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.internship.userservice.dto.AddCartRequestDto;
import com.innowise.internship.userservice.model.CardInfo;
import com.innowise.internship.userservice.model.User;
import com.innowise.internship.userservice.repository.CardInfoRepository;
import com.innowise.internship.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class CardInfoControllerIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>("postgres:15-alpine");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private CardInfoRepository cardInfoRepository;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    cardInfoRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void addCardToUser_whenDatsIsValid_shouldCreateCard() throws Exception {
    User user = initUser();
    User savedUser = userRepository.save(user);

    AddCartRequestDto requestDto = initAddCartRequestDto(savedUser.getId());

    mockMvc
        .perform(
            post("/api/v1/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.number").value("4242424242424242"))
        .andExpect(jsonPath("$.holder").value("SomeHolder"));
  }

  @Test
  public void addCardToUser_whenUserNotFound_shouldReturn404() throws Exception {
    AddCartRequestDto requestDto = initAddCartRequestDto(99L);

    mockMvc
        .perform(
            post("/api/v1/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.message")
                .value("User with " + requestDto.getUserId() + " id is not found"));
  }

  @Test
  public void getCardInfoById_whenCardExists_shouldReturnCard() throws Exception {
    User user = initUser();
    User savedUser = userRepository.save(user);

    CardInfo card = initCard(savedUser, "4242");
    CardInfo savedCard = cardInfoRepository.save(card);

    mockMvc
        .perform(get("/api/v1/cards/{id}", savedCard.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedCard.getId()))
        .andExpect(jsonPath("$.holder").value("SomeHolder"));
  }

  @Test
  public void getCardInfoById_whenCardNotFound_shouldReturn404() throws Exception {
    Long cardId = 99L;

    mockMvc
        .perform(get("/api/v1/cards/{id}", cardId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Card with " + cardId + " id is not found"));
  }

  @Test
  public void getCardsByUserId_whenUserExists_shouldReturnCardList() throws Exception {
    User user = initUser();
    User savedUser = userRepository.save(user);
    CardInfo card1 = initCard(savedUser, "1111");
    CardInfo card2 = initCard(savedUser, "2222");
    cardInfoRepository.saveAll(List.of(card1, card2));

    mockMvc
        .perform(get("/api/v1/cards?userId={userId}", savedUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  public void getCardsByUserId_whenUserDoesNotExists_shouldReturn404() throws Exception {
    Long userId = 99L;

    mockMvc
        .perform(get("/api/v1/cards?userId={userId}", userId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User with " + userId + " id is not found"));
  }

  @Test
  public void getCardsByIds_whenIdsAreProvided_shouldReturnCardList() throws Exception {
    User user = initUser();
    User savedUser = userRepository.save(user);
    CardInfo card1 = initCard(savedUser, "1111");
    CardInfo card2 = initCard(savedUser, "2222");
    cardInfoRepository.saveAll(List.of(card1, card2));

    mockMvc
        .perform(
            get("/api/v1/cards")
                .param("ids", card1.getId().toString() + "," + card2.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(card1.getId()))
        .andExpect(jsonPath("$[1].id").value(card2.getId()));
  }

  @Test
  public void updateCard_whereCardExists_ShouldUpdateAndReturnCard() throws Exception {
    User user = initUser();
    User savedUser = userRepository.save(user);
    CardInfo card = initCard(savedUser, "4242");
    CardInfo savedCard = cardInfoRepository.save(card);
    AddCartRequestDto updateRequest = initAddCartRequestDto(savedUser.getId());

    mockMvc
        .perform(
            put("/api/v1/cards/{id}", savedCard.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.holder").value("SomeHolder"))
        .andExpect(jsonPath("$.expirationDate").value("2030-01-01"));
  }

  @Test
  public void updateCard_whenCardDoesNotExist_ShouldReturn404() throws Exception {
    Long cardId = 99L;

    AddCartRequestDto updateRequest = initAddCartRequestDto(1L);

    mockMvc
        .perform(
            put("/api/v1/cards/{id}", cardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void deleteCard_whenCardExists_shouldDeleteCard() throws Exception {
    User user = initUser();
    User savedUser = userRepository.save(user);
    CardInfo card = initCard(savedUser, "4242");
    CardInfo savedCard = cardInfoRepository.save(card);

    mockMvc
        .perform(delete("/api/v1/cards/{id}", savedCard.getId()))
        .andExpect(status().isNoContent());

    assertThat(cardInfoRepository.findById(savedCard.getId())).isEmpty();
  }

  @Test
  public void deleteCard_whenCardDoesNotExist_shouldReturn404() throws Exception {
    Long cardId = 99L;

    mockMvc.perform(delete("/api/v1/cards/{id}", cardId)).andExpect(status().isNotFound());
  }

  public User initUser() {
    User user = new User();
    user.setName("SomeName");
    user.setSurname("Surname");
    user.setEmail("SomeEmail@SomeName.com");
    user.setBirth_date(LocalDate.of(1980, 1, 1));

    return user;
  }

  public CardInfo initCard(User user, String lastFourDigits) {
    CardInfo card = new CardInfo();
    card.setUser(user);
    card.setNumber("424242424242" + lastFourDigits);
    card.setHolder("SomeHolder");
    card.setExpirationDate(LocalDate.of(2030, 1, 1));

    return card;
  }

  public AddCartRequestDto initAddCartRequestDto(Long userId) {
    AddCartRequestDto requestDto = new AddCartRequestDto();
    requestDto.setUserId(userId);
    requestDto.setNumber("4242424242424242");
    requestDto.setHolder("SomeHolder");
    requestDto.setExpirationDate(LocalDate.of(2030, 1, 1));

    return requestDto;
  }
}
