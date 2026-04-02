package com.freelance.controller;

import com.freelance.dto.ApplicationCreateDto;
import com.freelance.dto.JobCreateDto;
import com.freelance.dto.LoginFormDto;
import com.freelance.dto.MessageCreateDto;
import com.freelance.dto.ProfileUpdateDto;
import com.freelance.dto.UserRegistrationDto;
import com.freelance.repository.ApplicationRepository;
import com.freelance.repository.JobRepository;
import com.freelance.repository.MessageRepository;
import com.freelance.repository.NotificationRepository;
import com.freelance.repository.ReviewRepository;
import com.freelance.repository.UserRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final ReviewRepository reviewRepository;

    public GlobalModelAttributes(UserRepository userRepository,
                                 ApplicationRepository applicationRepository,
                                 JobRepository jobRepository,
                                 MessageRepository messageRepository,
                                 NotificationRepository notificationRepository,
                                 ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
        this.reviewRepository = reviewRepository;
    }

    @ModelAttribute
    public void addCommonStats(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalJobs", jobRepository.count());
        model.addAttribute("totalApplications", applicationRepository.count());
        model.addAttribute("totalMessages", messageRepository.count());
        model.addAttribute("totalNotifications", notificationRepository.count());
        model.addAttribute("totalReviews", reviewRepository.count());
    }

    @ModelAttribute("loginForm")
    public LoginFormDto loginForm() {
        return new LoginFormDto();
    }

    @ModelAttribute("registerForm")
    public UserRegistrationDto registerForm() {
        return new UserRegistrationDto();
    }

    @ModelAttribute("applicationForm")
    public ApplicationCreateDto applicationForm() {
        return new ApplicationCreateDto();
    }

    @ModelAttribute("jobForm")
    public JobCreateDto jobForm() {
        return new JobCreateDto();
    }

    @ModelAttribute("messageForm")
    public MessageCreateDto messageForm() {
        return new MessageCreateDto();
    }

    @ModelAttribute("profileForm")
    public ProfileUpdateDto profileForm() {
        return new ProfileUpdateDto();
    }
}
