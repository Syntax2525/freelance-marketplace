package com.freelance.service;

import com.freelance.dto.NotificationResponseDto;
import com.freelance.model.Notification;
import com.freelance.model.User;
import com.freelance.repository.NotificationRepository;
import com.freelance.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<NotificationResponseDto> getCurrentUserNotifications(boolean unreadOnly, int page, int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        List<Notification> notifications;
        if (unreadOnly) {
            notifications = notificationRepository.findByUserIdAndReadFalse(user.getId(), pageable).getContent();
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable).getContent();
        }

        return notifications.stream()
                .map(this::mapToNotificationResponseDto)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        if (!notification.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only mark your own notifications as read");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponseDto mapToNotificationResponseDto(Notification n) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(n.getId());
        dto.setUserId(n.getUser().getId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setRead(n.isRead());
        dto.setRelatedEntityType(n.getRelatedEntityType());
        dto.setRelatedEntityId(n.getRelatedEntityId());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}