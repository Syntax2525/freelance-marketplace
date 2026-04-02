// com.freelance.model.Application.java
package com.freelance.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@EntityListeners(AuditingEntityListener.class)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    @Column(precision = 12, scale = 2)
    private BigDecimal proposedAmountTzs;

    @Column(length = 50)
    private String proposedTimeline;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime appliedAt;

    public Application() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public User getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(User freelancer) {
        this.freelancer = freelancer;
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

    public enum ApplicationStatus {
        PENDING, ACCEPTED, REJECTED, WITHDRAWN
    }
}
