package com.bankapp.controller;


import com.bankapp.dto.User.LoginRequestDto;
import com.bankapp.dto.User.LoginResponseDto;
import com.bankapp.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> userLogin(@RequestBody LoginRequestDto loginRequestDto){

        LoginResponseDto login = tokenService.login(loginRequestDto.username(), loginRequestDto.password());

        return ResponseEntity.ok(login);
    }
}
