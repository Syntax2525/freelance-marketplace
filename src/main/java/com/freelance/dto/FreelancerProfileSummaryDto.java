package com.freelance.dto;

import java.math.BigDecimal;

public class FreelancerProfileSummaryDto {

    private String headline;
    private BigDecimal hourlyRateTzs;
    private Integer yearsOfExperience;
    private String primaryCategory;
    private BigDecimal averageRating;
    private Integer completedJobs;

    // Default constructor
    public FreelancerProfileSummaryDto() {
    }

    // Getters and Setters
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public BigDecimal getHourlyRateTzs() {
        return hourlyRateTzs;
    }

    public void setHourlyRateTzs(BigDecimal hourlyRateTzs) {
        this.hourlyRateTzs = hourlyRateTzs;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getCompletedJobs() {
        return completedJobs;
    }

    public void setCompletedJobs(Integer completedJobs) {
        this.completedJobs = completedJobs;
    }
}