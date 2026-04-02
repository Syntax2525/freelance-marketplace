package com.freelance.dto;

import java.math.BigDecimal;

public class ReviewResponseDto {

    private Long id;
    private Long jobId;
    private Long reviewerId;
    private String reviewerName;
    private Long reviewedUserId;
    private String reviewedUserName;
    private BigDecimal rating;
    private String comment;

    public ReviewResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Long getReviewedUserId() {
        return reviewedUserId;
    }

    public void setReviewedUserId(Long reviewedUserId) {
        this.reviewedUserId = reviewedUserId;
    }

    public String getReviewedUserName() {
        return reviewedUserName;
    }

    public void setReviewedUserName(String reviewedUserName) {
        this.reviewedUserName = reviewedUserName;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}