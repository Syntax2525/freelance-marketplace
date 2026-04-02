package com.freelance.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class UserResponseDto {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String city;
    private String region;
    private String bio;
    private String profilePictureUrl;
    private boolean active;
    private boolean emailVerified;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional nested summary if user is freelancer
    private FreelancerProfileSummaryDto freelancerProfile;

    // Default constructor
    public UserResponseDto() {
    }

    // Full constructor (for manual creation or testing)
    public UserResponseDto(Long id, String email, String fullName, String phone, String city,
                           String region, String bio, boolean active, boolean emailVerified,
                           Set<String> roles, LocalDateTime createdAt, LocalDateTime updatedAt,
                           FreelancerProfileSummaryDto freelancerProfile) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.city = city;
        this.region = region;
        this.bio = bio;
        this.active = active;
        this.emailVerified = emailVerified;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.freelancerProfile = freelancerProfile;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public FreelancerProfileSummaryDto getFreelancerProfile() {
        return freelancerProfile;
    }

    public void setFreelancerProfile(FreelancerProfileSummaryDto freelancerProfile) {
        this.freelancerProfile = freelancerProfile;
    }
}