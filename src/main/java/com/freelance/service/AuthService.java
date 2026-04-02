package com.freelance.service;

import com.freelance.dto.AuthResponseDto;
import com.freelance.dto.LoginRequestDto;
import com.freelance.dto.UserRegistrationDto;
import com.freelance.dto.UserResponseDto;
import com.freelance.model.User;
import com.freelance.repository.UserRepository;
import com.freelance.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return new AuthResponseDto(token, mapToUserResponseDto(user));
    }

    public UserResponseDto register(UserRegistrationDto dto) {
        String email = dto.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setCity(dto.getCity());
        user.setRegion(dto.getRegion());
        user.setBio(dto.getBio());
        user.setActive(true);
        user.setEmailVerified(false);

        if (dto.getRole() != null && dto.getRole().trim().equalsIgnoreCase("admin")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot register as administrator");
        }
        String roleKey = "CLIENT".equalsIgnoreCase(dto.getRole()) ? "ROLE_CLIENT" : "ROLE_FREELANCER";
        user.setRoleName(roleKey);

        User saved = userRepository.save(user);

        return mapToUserResponseDto(saved);
    }

    private UserResponseDto mapToUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setCity(user.getCity());
        dto.setRegion(user.getRegion());
        dto.setBio(user.getBio());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setActive(user.isActive());
        dto.setEmailVerified(user.isEmailVerified());

        Set<String> roleNames = new HashSet<>();
        roleNames.add(user.getRoleName());
        dto.setRoles(roleNames);

        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }
}
