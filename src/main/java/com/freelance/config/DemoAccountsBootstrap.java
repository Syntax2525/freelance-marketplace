package com.freelance.config;

import com.freelance.model.Job;
import com.freelance.model.User;
import com.freelance.repository.JobRepository;
import com.freelance.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

@Configuration
public class DemoAccountsBootstrap {

    @Bean
    @Order(2)
    CommandLineRunner seedDemoAccounts(
            UserRepository userRepository,
            JobRepository jobRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            ensureUser(userRepository, "admin@freelance.com", "Demo platform administrator.",
                    passwordEncoder.encode("Admin12345"), "ROLE_ADMIN");
            User client = ensureUser(userRepository, "client@demo.com", "Startup founder and project lead.",
                    passwordEncoder.encode("Password123"), "ROLE_CLIENT");
            ensureUser(userRepository, "freelancer@demo.com", "Creative product designer.",
                    passwordEncoder.encode("Password123"), "ROLE_FREELANCER");

            if (jobRepository.count() == 0) {
                Job j1 = new Job();
                j1.setClient(client);
                j1.setTitle("E-commerce website redesign");
                j1.setDescription("Refresh the storefront with a modern and mobile-friendly interface.");
                j1.setCategory("design");
                j1.setBudgetTzs(new BigDecimal("2000000"));
                j1.setBudgetType("FIXED");
                j1.setLocationPreference("Remote");
                j1.setStatus(Job.JobStatus.OPEN);
                jobRepository.save(j1);

                Job j2 = new Job();
                j2.setClient(client);
                j2.setTitle("React dashboard build");
                j2.setDescription("Build an analytics dashboard for a new SaaS offering.");
                j2.setCategory("development");
                j2.setBudgetTzs(new BigDecimal("3500000"));
                j2.setBudgetType("FIXED");
                j2.setLocationPreference("Remote");
                j2.setStatus(Job.JobStatus.OPEN);
                jobRepository.save(j2);
            }
        };
    }

    private User ensureUser(UserRepository userRepository, String email, String bio,
                            String encodedPassword, String roleName) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        User u = new User();
        u.setEmail(email);
        u.setPassword(encodedPassword);
        u.setFullName(email.contains("admin") ? "Jordan Kemp"
                : email.contains("client") ? "Nora Patel" : "Ari Turner");
        u.setBio(bio);
        u.setActive(true);
        u.setRoleName(roleName);
        return userRepository.save(u);
    }
}
