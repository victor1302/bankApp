package com.bankapp.dto.User;

public record LoginResponseDto(String accessToken, Long expiresIn) {
}
