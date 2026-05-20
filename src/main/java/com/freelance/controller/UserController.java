package com.freelance.controller;

import com.freelance.dto.ConversationPartnerDto;
import com.freelance.dto.ProfileUpdateDto;
import com.freelance.dto.UserResponseDto;
import com.freelance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get current authenticated user details
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        UserResponseDto user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * Update current user's profile (all authenticated users)
     * Only users can update themselves
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@Valid @RequestBody ProfileUpdateDto dto) {
        return ResponseEntity.ok(userService.updateCurrentUser(dto));
    }

    /**
     * Upload profile picture for current user
     */
    @PostMapping(value = "/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.updateProfilePicture(file));
    }

    /**
     * Get list of users for messaging (directory)
     * Authenticated users only
     */
    @GetMapping("/directory")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ConversationPartnerDto>> directory() {
        return ResponseEntity.ok(userService.listDirectoryForMessaging());
    }

    /**
     * Get public user profile by ID
     * All authenticated users can view public profiles
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FREELANCER', 'ROLE_CLIENT')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Get freelancer profile by ID (public information)
     */
    @GetMapping("/{id}/freelancer-profile")
    public ResponseEntity<UserResponseDto> getFreelancerProfile(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Search users by name or email (authenticated users only)
     */
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam String query) {
        List<UserResponseDto> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    /**
     * Get user roles and permissions info (for frontend)
     */
    @GetMapping("/{id}/info")
    public ResponseEntity<Object> getUserInfo(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserInfo(id));
    }
}