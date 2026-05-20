package com.freelance.service;

import com.freelance.dto.JobCreateDto;
import com.freelance.dto.JobResponseDto;
import com.freelance.model.Job;
import com.freelance.model.User;
import com.freelance.repository.JobRepository;
import com.freelance.repository.UserRepository;
import com.freelance.util.RoleGuard;
import com.freelance.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new job (CLIENT only)
     */
    public JobResponseDto createJob(JobCreateDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        
        // Ensure only CLIENT can create jobs
        RoleGuard.requireRole(client, RoleGuard.ROLE_CLIENT);
        RoleGuard.forbidFreelancer(client);
        RoleGuard.forbidAdmin(client);

        Job job = new Job();
        job.setClient(client);
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setCategory(dto.getCategory());
        job.setBudgetTzs(dto.getBudgetTzs());
        job.setBudgetType(dto.getBudgetType());
        job.setLocationPreference(dto.getLocationPreference());
        job.setStatus(Job.JobStatus.OPEN);

        Job saved = jobRepository.save(job);

        return mapToJobResponseDto(saved);
    }

    /**
     * Get job by ID (public read)
     */
    public JobResponseDto getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found with id: " + id));

        return mapToJobResponseDto(job);
    }

    /**
     * Get all jobs with optional status filtering (public read)
     */
    public Page<JobResponseDto> getAllJobs(Job.JobStatus status, Pageable pageable) {
        Page<Job> jobs;
        if (status != null) {
            jobs = jobRepository.findByStatus(status, pageable);
        } else {
            jobs = jobRepository.findAll(pageable);
        }

        return jobs.map(this::mapToJobResponseDto);
    }

    /**
     * Get current user's jobs
     * - CLIENT sees own created jobs
     * - FREELANCER cannot create jobs, so this should be handled differently
     */
    public Page<JobResponseDto> getCurrentUserJobs(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // For CLIENT: return their created jobs
        if (RoleGuard.isClient(user)) {
            Page<Job> jobs = jobRepository.findByClientId(user.getId(), pageable);
            return jobs.map(this::mapToJobResponseDto);
        }
        
        // For FREELANCER: could return jobs they've applied to or viewed jobs
        // This is business logic specific
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "This endpoint is for clients to view their own jobs");
    }

    /**
     * Update a job (CLIENT only - must own the job)
     */
    public JobResponseDto updateJob(Long jobId, JobCreateDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        // Ensure only CLIENT can update jobs
        RoleGuard.requireRole(client, RoleGuard.ROLE_CLIENT);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found with id: " + jobId));

        // Verify ownership
        if (!job.getClient().getId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "You can only update your own jobs");
        }

        // Prevent updates to jobs that are already in progress or completed
        if (job.getStatus() == Job.JobStatus.IN_PROGRESS || 
            job.getStatus() == Job.JobStatus.COMPLETED || 
            job.getStatus() == Job.JobStatus.DISPUTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot update a job that is in progress, completed, or disputed");
        }

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setCategory(dto.getCategory());
        job.setBudgetTzs(dto.getBudgetTzs());
        job.setBudgetType(dto.getBudgetType());
        job.setLocationPreference(dto.getLocationPreference());

        Job updated = jobRepository.save(job);
        return mapToJobResponseDto(updated);
    }

    /**
     * Delete a job (CLIENT only - must own the job)
     */
    public void deleteJob(Long jobId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        // Ensure only CLIENT can delete jobs
        RoleGuard.requireRole(client, RoleGuard.ROLE_CLIENT);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found with id: " + jobId));

        // Verify ownership
        if (!job.getClient().getId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "You can only delete your own jobs");
        }

        // Prevent deletion of jobs that are already in progress
        if (job.getStatus() == Job.JobStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot delete a job that is currently in progress");
        }

        jobRepository.deleteById(jobId);
    }

    /**
     * Close a job (CLIENT who owns it or ADMIN)
     */
    public JobResponseDto closeJob(Long jobId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found with id: " + jobId));

        // Only CLIENT who owns the job or ADMIN can close it
        if (RoleGuard.isClient(user)) {
            if (!job.getClient().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                        "You can only close your own jobs");
            }
        } else if (!RoleGuard.isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Only the job client or admin can close a job");
        }

        job.setStatus(Job.JobStatus.CANCELLED);
        Job updated = jobRepository.save(job);
        return mapToJobResponseDto(updated);
    }

    /**
     * Search jobs by category
     */
    public Page<JobResponseDto> searchByCategory(String category, Pageable pageable) {
        Page<Job> jobs = jobRepository.findByCategory(category, pageable);
        return jobs.map(this::mapToJobResponseDto);
    }

    private JobResponseDto mapToJobResponseDto(Job job) {
        JobResponseDto dto = new JobResponseDto();
        dto.setId(job.getId());
        dto.setClientId(job.getClient().getId());
        dto.setClientName(job.getClient().getFullName());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setCategory(job.getCategory());
        dto.setBudgetTzs(job.getBudgetTzs());
        dto.setBudgetType(job.getBudgetType());
        dto.setLocationPreference(job.getLocationPreference());
        dto.setStatus(job.getStatus());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setUpdatedAt(job.getUpdatedAt());
        return dto;
    }
}
