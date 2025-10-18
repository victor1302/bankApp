package com.bankapp.service;

import com.bankapp.entity.Role;
import com.bankapp.entity.User;
import com.bankapp.exception.UserAlreadyExistsException;
import com.bankapp.exception.UserAlreadyIsDisableOrNotPresent;
import com.bankapp.interfaces.UserProjection;
import com.bankapp.repository.RoleRepository;
import com.bankapp.repository.UserRepository;
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
    private final PasswordEncoder passwordEncoder;


    public UserService(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(String username, String password, String phoneNumber, String address, int age){
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        if(userRepository.existsByUsername(username)){
            throw new UserAlreadyExistsException("Usuário já existe!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        user.setAge(age);
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
                    user.setActive(false);
                    return userRepository.save(user);
                })
                .orElseThrow(()-> new UserAlreadyExistsException("a"));
    }


}
