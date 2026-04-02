package com.freelance.dto;

import com.freelance.model.Application.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ApplicationResponseDto {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String clientName;
    private Long freelancerId;
    private String freelancerName;
    private String coverLetter;
    private BigDecimal proposedAmountTzs;
    private String proposedTimeline;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    public ApplicationResponseDto() {
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

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getFreelancerId() {
        return freelancerId;
    }

    public void setFreelancerId(Long freelancerId) {
        this.freelancerId = freelancerId;
    }

    public String getFreelancerName() {
        return freelancerName;
    }

    public void setFreelancerName(String freelancerName) {
        this.freelancerName = freelancerName;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public BigDecimal getProposedAmountTzs() {
        return proposedAmountTzs;
    }

    public void setProposedAmountTzs(BigDecimal proposedAmountTzs) {
        this.proposedAmountTzs = proposedAmountTzs;
    }

    public String getProposedTimeline() {
        return proposedTimeline;
    }

    public void setProposedTimeline(String proposedTimeline) {
        this.proposedTimeline = proposedTimeline;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}