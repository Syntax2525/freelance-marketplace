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

    @PostMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('ROLE_FREELANCER')")
    public ResponseEntity<ApplicationResponseDto> submitApplication(
            @PathVariable Long jobId,
            @RequestBody ApplicationCreateDto dto) {

        ApplicationResponseDto application = applicationService.submitApplication(jobId, dto);
        return new ResponseEntity<>(application, HttpStatus.CREATED);
    }

    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<List<ApplicationResponseDto>> getApplicationsForJob(@PathVariable Long jobId) {
        List<ApplicationResponseDto> applications = applicationService.getApplicationsForJob(jobId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('ROLE_FREELANCER')")
    public ResponseEntity<List<ApplicationResponseDto>> getMyApplications() {
        List<ApplicationResponseDto> myApplications = applicationService.getCurrentUserApplications();
        return ResponseEntity.ok(myApplications);
    }

    // More endpoints: accept/reject application, withdraw application
}