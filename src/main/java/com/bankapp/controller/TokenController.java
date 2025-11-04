package com.bankapp.controller;


import com.bankapp.dto.User.LoginRequestDto;
import com.bankapp.dto.User.LoginResponseDto;
import com.bankapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final UserService userService;

    public TokenController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> userLogin(@RequestBody LoginRequestDto loginRequestDto){

        LoginResponseDto login = userService.loginUser(loginRequestDto);

        return ResponseEntity.ok(login);
    }

}
