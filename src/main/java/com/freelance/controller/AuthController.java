package com.freelance.controller;

import com.freelance.dto.AuthResponseDto;
import com.freelance.dto.LoginRequestDto;
import com.freelance.dto.UserRegistrationDto;
import com.freelance.dto.UserResponseDto;
import com.freelance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        AuthResponseDto response = authService.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegistrationDto registrationDto) {

        UserResponseDto response = authService.register(registrationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}