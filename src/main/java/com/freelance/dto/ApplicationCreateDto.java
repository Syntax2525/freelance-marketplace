package com.freelance.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class ApplicationCreateDto {

    @NotBlank(message = "Cover letter is required")
    private String coverLetter;

    private BigDecimal proposedAmountTzs;

    private String proposedTimeline;

    public ApplicationCreateDto() {
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
}