package com.bankapp.controller;

import com.bankapp.dto.User.CreateUserDto;
import com.bankapp.entity.User;
import com.bankapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDto createUserDto){
        userService.createUser(createUserDto.username(), createUserDto.password(), createUserDto.phoneNumber(), createUserDto.address(), createUserDto.age());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listUsers(){
        List<User> users = userService.listUsers();
        return ResponseEntity.ok(users);
    }
}
