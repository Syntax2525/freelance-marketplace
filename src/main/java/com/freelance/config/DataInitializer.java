package com.freelance.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Roles are stored on {@link com.freelance.model.User#roleName}; skill labels for forms use
 * {@link com.freelance.config.SkillCatalog}. No DB seed rows required here.
 */
@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Intentionally empty; demo accounts are created in DemoAccountsBootstrap.
    }
}
