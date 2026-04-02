package com.freelance.controller;

import com.freelance.dto.LoginFormDto;
import com.freelance.dto.UserRegistrationDto;
import com.freelance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthViewController {

    private final AuthService authService;

    public AuthViewController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@Valid UserRegistrationDto registerForm,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerForm", registerForm);
            model.addAttribute("loginForm", new LoginFormDto());
            return "register_page";
        }
        try {
            authService.register(registerForm);
        } catch (RuntimeException ex) {
            bindingResult.rejectValue("email", "duplicate", ex.getMessage());
            model.addAttribute("registerForm", registerForm);
            model.addAttribute("loginForm", new LoginFormDto());
            return "register_page";
        }
        return "redirect:/login?registered";
    }
}
