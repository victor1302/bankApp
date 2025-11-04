package com.bankapp.dto.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserDto(@NotNull String username,
                            @NotNull @Size(max = 8) String password,
                            @Size(max = 12) String phoneNumber,
                            @Size(max = 30) String address,
                            @NotNull String email,
                            @NotNull int age) {
}
