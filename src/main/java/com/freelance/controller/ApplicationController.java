package com.freelance.controller;

import com.freelance.dto.ApplicationCreateDto;
import com.freelance.dto.ApplicationResponseDto;
import com.freelance.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * Submit application for a job (FREELANCER only)
     */
    @PostMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('ROLE_FREELANCER')")
    public ResponseEntity<ApplicationResponseDto> submitApplication(
            @PathVariable Long jobId,
            @RequestBody ApplicationCreateDto dto) {

        ApplicationResponseDto application = applicationService.submitApplication(jobId, dto);
        return new ResponseEntity<>(application, HttpStatus.CREATED);
    }

    /**
     * Get applications for a job (CLIENT who owns the job, ADMIN access)
     */
    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationResponseDto>> getApplicationsForJob(@PathVariable Long jobId) {
        List<ApplicationResponseDto> applications = applicationService.getApplicationsForJob(jobId);
        return ResponseEntity.ok(applications);
    }

    /**
     * Get current user's applications (FREELANCER only)
     */
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('ROLE_FREELANCER')")
    public ResponseEntity<List<ApplicationResponseDto>> getMyApplications() {
        List<ApplicationResponseDto> myApplications = applicationService.getCurrentUserApplications();
        return ResponseEntity.ok(myApplications);
    }

    /**
     * Accept an application (CLIENT who owns the job, ADMIN access)
     */
    @PostMapping("/{applicationId}/accept")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<ApplicationResponseDto> acceptApplication(@PathVariable Long applicationId) {
        ApplicationResponseDto accepted = applicationService.acceptApplication(applicationId);
        return ResponseEntity.ok(accepted);
    }

    /**
     * Reject an application (CLIENT who owns the job, ADMIN access)
     */
    @PostMapping("/{applicationId}/reject")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<ApplicationResponseDto> rejectApplication(@PathVariable Long applicationId) {
        ApplicationResponseDto rejected = applicationService.rejectApplication(applicationId);
        return ResponseEntity.ok(rejected);
    }

    /**
     * Withdraw an application (FREELANCER who submitted it)
     */
    @PostMapping("/{applicationId}/withdraw")
    @PreAuthorize("hasRole('ROLE_FREELANCER')")
    public ResponseEntity<ApplicationResponseDto> withdrawApplication(@PathVariable Long applicationId) {
        ApplicationResponseDto withdrawn = applicationService.withdrawApplication(applicationId);
        return ResponseEntity.ok(withdrawn);
    }

    /**
     * Get single application details (APPLICATION owner or CLIENT/ADMIN)
     */
    @GetMapping("/{applicationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApplicationResponseDto> getApplication(@PathVariable Long applicationId) {
        ApplicationResponseDto application = applicationService.getApplication(applicationId);
        return ResponseEntity.ok(application);
    }
}