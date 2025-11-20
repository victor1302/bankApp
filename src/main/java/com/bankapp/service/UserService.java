package com.bankapp.service;

import com.bankapp.dto.User.LoginRequestDto;
import com.bankapp.dto.User.LoginResponseDto;
import com.bankapp.entity.Role;
import com.bankapp.entity.User;
import com.bankapp.exception.AlreadyExistsException;
import com.bankapp.exception.AlreadyDisabledOrNotPresent;
import com.bankapp.interfaces.UserProjection;
import com.bankapp.repository.RoleRepository;
import com.bankapp.repository.UserRepository;
import com.bankapp.security.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;


    public UserService(RoleRepository roleRepository, UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto){
        User user = this.userRepository.findByEmail(loginRequestDto.email()).orElseThrow(() -> new RuntimeException("Credentials Invalid"));
        if(!user.isActive()){
            throw new AlreadyExistsException("User is disabled or not exists");
        }
        if(passwordEncoder.matches(loginRequestDto.password(), user.getPassword())){
            String token = this.tokenService.generateToken(user);
            return new LoginResponseDto(user.getEmail(), token);
        }
        throw new RuntimeException("Invalid credentials");
    }


    @Transactional
    public User createUser(String username, String password, String phoneNumber, String address, int age, String email){
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        if(userRepository.existsByUsername(username)){
            throw new AlreadyExistsException("User already exists!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        user.setAge(age);
        user.setEmail(email);
        user.setActive(true);
        user.setUserRole(Set.of(basicRole));
        return userRepository.save(user);
    }
    @Transactional
    public Page<UserProjection> getUsers(Pageable pageable){
        return userRepository.findAllBy(pageable);
    }
    @Transactional
    public User disableUser(UUID userId){
        return userRepository.findById(userId)
                .map(user ->{
                    if(!user.isActive() || user.getUserAccount().isActive()){
                        throw new AlreadyDisabledOrNotPresent("User not present or already disabled or have an active account");
                    }
                    user.setActive(false);
                    return userRepository.save(user);
                })
                .orElseThrow(()-> new AlreadyDisabledOrNotPresent("User not present or already disabled"));
    }


}
