// com.freelance.model.FreelancerProfile.java
package com.freelance.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "freelancer_profiles")
public class FreelancerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @MapsId
    private User user;

    @Column(length = 300)
    private String headline;                    // "Mobile App Developer | Flutter & React Native | Dar es Salaam"

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(precision = 10, scale = 2)
    private BigDecimal hourlyRateTzs;           // in TZS

    @Column
    private Integer yearsOfExperience;

    @Column(length = 120)
    private String primaryCategory;             // "Mobile Development", "Graphic Design", "Web Development", ...

    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column
    private Integer completedJobs = 0;

    @Column
    private Integer totalEarningsTzs;           // in TZS (cumulative)

    @Column(length = 500)
    private String portfolioUrl;                // external link (Behance, GitHub, personal site, ...)

    @Column
    private String profilePictureUrl;

    public FreelancerProfile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
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

    public Integer getTotalEarningsTzs() {
        return totalEarningsTzs;
    }

    public void setTotalEarningsTzs(Integer totalEarningsTzs) {
        this.totalEarningsTzs = totalEarningsTzs;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
