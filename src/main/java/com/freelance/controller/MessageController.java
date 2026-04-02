package com.freelance.controller;

import com.freelance.dto.ConversationPartnerDto;
import com.freelance.dto.MessageCreateDto;
import com.freelance.dto.MessageResponseDto;
import com.freelance.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/partners")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ConversationPartnerDto>> getPartners() {
        return ResponseEntity.ok(messageService.getConversationPartners());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseDto> sendMessage(@RequestBody MessageCreateDto dto) {
        MessageResponseDto message = messageService.sendMessage(dto);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/conversation/{otherUserId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponseDto>> getConversationWithUser(
            @PathVariable Long otherUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<MessageResponseDto> messages = messageService.getConversation(otherUserId, page, size);
        return ResponseEntity.ok(messages);
    }

    // Real-time chat usually uses WebSocket → this is REST fallback / history
}