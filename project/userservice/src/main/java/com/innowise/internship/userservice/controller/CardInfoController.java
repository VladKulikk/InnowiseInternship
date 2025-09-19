package com.innowise.internship.userservice.controller;

import com.innowise.internship.userservice.dto.AddCartRequestDto;
import com.innowise.internship.userservice.dto.CardInfoResponseDto;
import com.innowise.internship.userservice.service.CardInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards") // Sets the base URL for all endpoints in this class
@RequiredArgsConstructor
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardInfoResponseDto addCard(@Valid @RequestBody AddCartRequestDto requestDto) {
        return cardInfoService.addCartToUser(requestDto);
    }

    @GetMapping("/{id}")
    public CardInfoResponseDto getCardById(@PathVariable Long id) {
        return cardInfoService.getCardInfoById(id);
    }

    // Example URL: /api/v1/cards?userId=1
    @GetMapping(params = "userId")
    public List<CardInfoResponseDto> getCardsByUserId(@RequestParam Long userId) {
        return cardInfoService.getCardsInfoByUserId(userId);
    }

    @GetMapping
    public List<CardInfoResponseDto> getAllCards() {
        return cardInfoService.getAllCardsInfo();
    }

    @GetMapping
    public List<CardInfoResponseDto> getCardsByIds(@RequestParam List<Long> ids) {
        return cardInfoService.getCardsInfoByIds(ids);
    }

    @PutMapping("/{id}")
    public CardInfoResponseDto updateCard(@PathVariable Long id, @Valid @RequestBody AddCartRequestDto requestDto) {
        return cardInfoService.updateCardInfo(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long id) {
        cardInfoService.deleteCard(id);
    }
}
