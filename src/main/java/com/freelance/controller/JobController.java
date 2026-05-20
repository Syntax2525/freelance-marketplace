package com.freelance.controller;
import com.freelance.dto.JobCreateDto;
import com.freelance.dto.JobResponseDto;
import com.freelance.model.Job;
import com.freelance.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Create a new job (CLIENT only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<JobResponseDto> createJob(@RequestBody JobCreateDto dto) {
        JobResponseDto created = jobService.createJob(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get job by ID (publicly accessible)
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDto> getJobById(@PathVariable Long id) {
        JobResponseDto job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    /**
     * Get all jobs with optional filtering (publicly accessible)
     */
    @GetMapping
    public ResponseEntity<Page<JobResponseDto>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Job.JobStatus status) {

        Pageable pageable = PageRequest.of(page, size);
        Page<JobResponseDto> jobs = jobService.getAllJobs(status, pageable);
        return ResponseEntity.ok(jobs);
    }

    /**
     * Get current user's jobs (CLIENT views own jobs, FREELANCER views own bidded jobs)
     */
    @GetMapping("/my-jobs")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_FREELANCER')")
    public ResponseEntity<Page<JobResponseDto>> getMyJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<JobResponseDto> myJobs = jobService.getCurrentUserJobs(pageable);
        return ResponseEntity.ok(myJobs);
    }

    /**
     * Update a job (CLIENT only - owns the job)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<JobResponseDto> updateJob(
            @PathVariable Long id,
            @RequestBody JobCreateDto dto) {
        JobResponseDto updated = jobService.updateJob(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a job (CLIENT only - owns the job)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Close a job (CLIENT only - owns the job, ADMIN has access)
     */
    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<JobResponseDto> closeJob(@PathVariable Long id) {
        JobResponseDto closed = jobService.closeJob(id);
        return ResponseEntity.ok(closed);
    }

    /**
     * Search jobs by category
     */
    @GetMapping("/search/category/{category}")
    public ResponseEntity<Page<JobResponseDto>> searchByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<JobResponseDto> jobs = jobService.searchByCategory(category, pageable);
        return ResponseEntity.ok(jobs);
    }
}