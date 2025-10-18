package com.bankapp.controller;
import com.bankapp.dto.User.CreateUserDto;
import com.bankapp.interfaces.UserProjection;
import com.bankapp.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


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
    public ResponseEntity<Page<UserProjection>> getUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProjection> users = userService.getUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/user/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> disableUser(@PathVariable UUID id){
        userService.disableUser(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
