package com.bankapp.controller;

import com.bankapp.dto.Card.CardCreateResponseDto;
import com.bankapp.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/card")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BASIC')")
    public ResponseEntity<CardCreateResponseDto> createCard(){
        CardCreateResponseDto cardCreateResponseDto = cardService.createCard();
        return ResponseEntity.ok(cardCreateResponseDto);
    }
}
