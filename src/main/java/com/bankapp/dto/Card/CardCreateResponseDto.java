package com.bankapp.dto.Card;

import java.time.Instant;

public record CardCreateResponseDto(String pan, String cvv, String expiry) {
}
