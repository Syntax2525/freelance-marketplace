package com.freelance.controller;

import com.freelance.repository.ApplicationRepository;
import com.freelance.repository.JobRepository;
import com.freelance.repository.MessageRepository;
import com.freelance.repository.NotificationRepository;
import com.freelance.repository.ReviewRepository;
import com.freelance.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Legacy Thymeleaf dashboard (optional). Static SPA is served from /index.html.
 */
@Controller
public class UiController {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final ReviewRepository reviewRepository;
    private final ApplicationRepository applicationRepository;

    public UiController(JobRepository jobRepository,
                        UserRepository userRepository,
                        MessageRepository messageRepository,
                        NotificationRepository notificationRepository,
                        ReviewRepository reviewRepository,
                        ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
        this.reviewRepository = reviewRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/ui")
    public String uiIndex(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalJobs", jobRepository.count());
        model.addAttribute("totalApplications", applicationRepository.count());
        model.addAttribute("totalMessages", messageRepository.count());
        model.addAttribute("totalNotifications", notificationRepository.count());
        model.addAttribute("totalReviews", reviewRepository.count());
        return "index";
    }
}
