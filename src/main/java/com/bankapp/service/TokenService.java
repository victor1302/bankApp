package com.bankapp.service;

import com.bankapp.dto.User.LoginResponseDto;
import com.bankapp.entity.Role;
import com.bankapp.entity.User;
import com.bankapp.exception.BadCredentialException;
import com.bankapp.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenService(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public LoginResponseDto login(String username, String password){

        var user = userRepository.findByUsername(username);

        if(user.isEmpty() || !user.get().isLoginCorret(password, bCryptPasswordEncoder)){
            throw new BadCredentialException("User ou password is invalid!");
        }

        var scope = user.get().getUserRole()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(""));

        var now = Instant.now();
        var expiresIn = 300L;
        var claims = JwtClaimsSet.builder()
                .issuer("bankApp")
                .subject(user.get().getUserId().toString())
                .issuedAt(now)
                .claim("scope", scope)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();
        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponseDto(jwtValue, expiresIn);
    }
}
