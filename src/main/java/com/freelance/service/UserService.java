package com.freelance.service;

import com.freelance.dto.ConversationPartnerDto;
import com.freelance.dto.ProfileUpdateDto;
import com.freelance.dto.UserResponseDto;
import com.freelance.model.User;
import com.freelance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final long MAX_PROFILE_IMAGE_BYTES = 3 * 1024 * 1024;
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private final UserRepository userRepository;
    private final Path uploadRoot;

    public UserService(UserRepository userRepository,
                       @Value("${app.upload.dir:uploads}") String uploadDir) {
        this.userRepository = userRepository;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public UserResponseDto updateCurrentUser(ProfileUpdateDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        user.setFullName(dto.getFullName());
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String newEmail = dto.getEmail().trim().toLowerCase();
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email is already in use");
            }
            user.setEmail(newEmail);
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getCity() != null) {
            user.setCity(dto.getCity());
        }
        if (dto.getRegion() != null) {
            user.setRegion(dto.getRegion());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }

        User saved = userRepository.save(user);
        return mapToUserResponseDto(saved);
    }

    public UserResponseDto updateProfilePicture(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please choose an image file");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Allowed types: JPEG, PNG, GIF, WebP");
        }
        if (file.getSize() > MAX_PROFILE_IMAGE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image must be 3 MB or smaller");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        String ext = extensionForContentType(contentType);
        String filename = user.getId() + "-" + UUID.randomUUID() + ext;
        Path profilesDir = uploadRoot.resolve("profiles");
        try {
            Files.createDirectories(profilesDir);
            Path target = profilesDir.resolve(filename);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            deleteStoredProfileFile(user.getProfilePictureUrl());

            String publicPath = "/uploads/profiles/" + filename;
            user.setProfilePictureUrl(publicPath);
            return mapToUserResponseDto(userRepository.save(user));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image");
        }
    }

    private void deleteStoredProfileFile(String previousUrl) {
        if (previousUrl == null || !previousUrl.startsWith("/uploads/profiles/")) {
            return;
        }
        String name = previousUrl.substring("/uploads/profiles/".length());
        if (name.contains("..") || name.contains("/") || name.contains("\\")) {
            return;
        }
        try {
            Files.deleteIfExists(uploadRoot.resolve("profiles").resolve(name));
        } catch (IOException ignored) {
        }
    }

    private static String extensionForContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".bin";
        };
    }

    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // username = email in JWT setup

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        return mapToUserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<ConversationPartnerDto> listDirectoryForMessaging() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User me = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        return userRepository.findAll(PageRequest.of(0, 200)).stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .map(u -> new ConversationPartnerDto(u.getId(), u.getFullName(), u.getEmail()))
                .sorted(Comparator.comparing(ConversationPartnerDto::getFullName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return mapToUserResponseDto(user);
    }

    // Reuse the same mapping method from AuthService
    // (you can move it to a shared Mapper class later)
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
