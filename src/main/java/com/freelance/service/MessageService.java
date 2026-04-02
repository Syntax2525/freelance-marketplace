package com.freelance.service;

import com.freelance.dto.ConversationPartnerDto;
import com.freelance.dto.MessageCreateDto;
import com.freelance.dto.MessageResponseDto;
import com.freelance.model.Message;
import com.freelance.model.User;
import com.freelance.repository.MessageRepository;
import com.freelance.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public MessageResponseDto sendMessage(MessageCreateDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String senderEmail = auth.getName();

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(dto.getContent());
        message.setRead(false);
        message.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);

        return mapToMessageResponseDto(saved);
    }

    public List<MessageResponseDto> getConversation(Long otherUserId, int page, int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        Pageable pageable = PageRequest.of(page, size);

        List<Message> messages = messageRepository
                .findConversationBetween(currentUser.getId(), otherUserId, pageable)
                .getContent();

        List<MessageResponseDto> dtos = messages.stream()
                .map(this::mapToMessageResponseDto)
                .collect(Collectors.toList());
        Collections.reverse(dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<ConversationPartnerDto> getConversationPartners() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        List<Long> partnerIds = messageRepository.findPartnerUserIds(currentUser.getId());
        if (partnerIds.isEmpty()) {
            return Collections.emptyList();
        }

        return partnerIds.stream()
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(u -> u != null)
                .map(u -> new ConversationPartnerDto(u.getId(), u.getFullName(), u.getEmail()))
                .sorted(Comparator.comparing(ConversationPartnerDto::getFullName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private MessageResponseDto mapToMessageResponseDto(Message msg) {
        MessageResponseDto dto = new MessageResponseDto();
        dto.setId(msg.getId());
        dto.setSenderId(msg.getSender().getId());
        dto.setSenderName(msg.getSender().getFullName());
        dto.setReceiverId(msg.getReceiver().getId());
        dto.setReceiverName(msg.getReceiver().getFullName());
        dto.setContent(msg.getContent());
        dto.setRead(msg.isRead());
        dto.setSentAt(msg.getSentAt());
        return dto;
    }
}