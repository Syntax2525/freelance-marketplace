package com.freelance.service;

import com.freelance.dto.FreelancerProfileSummaryDto;
import com.freelance.dto.ProfileUpdateDto;
import com.freelance.model.FreelancerProfile;
import com.freelance.model.User;
import com.freelance.repository.FreelancerProfileRepository;
import com.freelance.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class FreelancerProfileService {

    private final FreelancerProfileRepository profileRepository;
    private final UserRepository userRepository;

    public FreelancerProfileService(FreelancerProfileRepository profileRepository,
                                    UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    // Example: get current freelancer profile summary
    public FreelancerProfileSummaryDto getCurrentFreelancerProfileSummary() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FreelancerProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Freelancer profile not found"));

        FreelancerProfileSummaryDto dto = new FreelancerProfileSummaryDto();
        dto.setHeadline(profile.getHeadline());
        dto.setHourlyRateTzs(profile.getHourlyRateTzs());
        dto.setYearsOfExperience(profile.getYearsOfExperience());
        dto.setPrimaryCategory(profile.getPrimaryCategory());
        dto.setAverageRating(profile.getAverageRating());
        dto.setCompletedJobs(profile.getCompletedJobs());

        return dto;
    }

    public void upsertProfile(ProfileUpdateDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setCity(dto.getCity());
        user.setRegion(dto.getRegion());
        user.setBio(dto.getBio());
        userRepository.save(user);

        FreelancerProfile profile = profileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    FreelancerProfile p = new FreelancerProfile();
                    p.setUser(user);
                    return p;
                });

        profile.setHeadline(dto.getHeadline());
        profile.setOverview(dto.getOverview());
        profile.setHourlyRateTzs(dto.getHourlyRateTzs());
        profile.setYearsOfExperience(dto.getYearsOfExperience());
        profile.setPrimaryCategory(dto.getPrimaryCategory());
        profile.setPortfolioUrl(dto.getPortfolioUrl());
        profile.setProfilePictureUrl(dto.getProfilePictureUrl());

        profileRepository.save(profile);
    }

    // More methods: create/update profile, add portfolio links, etc.
}
