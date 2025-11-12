package com.bankapp.controller;

import com.bankapp.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/card")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BASIC')")
    public ResponseEntity<Void> createCard(){
        cardService.createCard();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
