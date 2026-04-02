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

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<JobResponseDto> createJob(@RequestBody JobCreateDto dto) {
        JobResponseDto created = jobService.createJob(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDto> getJobById(@PathVariable Long id) {
        JobResponseDto job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    @GetMapping
    public ResponseEntity<Page<JobResponseDto>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Job.JobStatus status) {

        Pageable pageable = PageRequest.of(page, size);
        Page<JobResponseDto> jobs = jobService.getAllJobs(status, pageable);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<JobResponseDto>> getMyJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<JobResponseDto> myJobs = jobService.getCurrentUserJobs(pageable);
        return ResponseEntity.ok(myJobs);
    }

    // More endpoints: update job, close job, delete job, search by category/budget/etc.
}