package com.bankapp.dto.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(@NotNull String username, @NotNull String password) {
}
