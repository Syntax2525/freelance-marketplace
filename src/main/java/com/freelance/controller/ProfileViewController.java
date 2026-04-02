package com.freelance.controller;

import com.freelance.dto.ProfileUpdateDto;
import com.freelance.model.User;
import com.freelance.repository.FreelancerProfileRepository;
import com.freelance.repository.UserRepository;
import com.freelance.service.FreelancerProfileService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class ProfileViewController {

    private final FreelancerProfileService profileService;
    private final UserRepository userRepository;
    private final FreelancerProfileRepository profileRepository;

    public ProfileViewController(FreelancerProfileService profileService,
                                 UserRepository userRepository,
                                 FreelancerProfileRepository profileRepository) {
        this.profileService = profileService;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @PostMapping("/freelancer/profile")
    public String updateProfile(@Valid ProfileUpdateDto profileForm,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("profileForm", profileForm);
            Optional<User> user = userRepository.findByEmail(
                    org.springframework.security.core.context.SecurityContextHolder.getContext()
                            .getAuthentication().getName());
            user.ifPresent(u -> {
                model.addAttribute("profileUser", u);
                profileRepository.findByUserId(u.getId())
                        .ifPresent(p -> model.addAttribute("freelancerProfile", p));
            });
            return "freelancer_profile";
        }
        profileService.upsertProfile(profileForm);
        return "redirect:/freelancer/profile?updated";
    }
}
