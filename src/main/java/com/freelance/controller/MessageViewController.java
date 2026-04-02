package com.freelance.controller;

import com.freelance.dto.MessageCreateDto;
import com.freelance.repository.MessageRepository;
import com.freelance.repository.UserRepository;
import com.freelance.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MessageViewController {

    private final MessageService messageService;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageViewController(MessageService messageService,
                                 MessageRepository messageRepository,
                                 UserRepository userRepository) {
        this.messageService = messageService;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/messages/send")
    public String sendMessage(@Valid MessageCreateDto messageForm,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("messageForm", messageForm);
            model.addAttribute("messages", messageRepository.findAll());
            model.addAttribute("allUsers", userRepository.findAll());
            return "messaging";
        }
        messageService.sendMessage(messageForm);
        return "redirect:/messages?sent";
    }
}
