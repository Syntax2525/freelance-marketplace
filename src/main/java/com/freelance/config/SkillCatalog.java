package com.freelance.config;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Skill labels for job forms (replaces the persisted {@code skills} catalog table).
 */
@Component
public class SkillCatalog {

    private static final List<String> NAMES = List.of(
            "Graphic Design",
            "UI/UX",
            "Web Development",
            "Mobile Development",
            "Photography",
            "Video Editing",
            "Copywriting",
            "Marketing"
    );

    public List<String> getAllSkillNames() {
        return NAMES;
    }
}
