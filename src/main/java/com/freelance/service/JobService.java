package com.freelance.service;

import com.freelance.dto.JobCreateDto;
import com.freelance.dto.JobResponseDto;
import com.freelance.model.Job;
import com.freelance.model.User;
import com.freelance.repository.JobRepository;
import com.freelance.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public JobResponseDto createJob(JobCreateDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

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

    public JobResponseDto getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        return mapToJobResponseDto(job);
    }

    public Page<JobResponseDto> getAllJobs(Job.JobStatus status, Pageable pageable) {
        Page<Job> jobs;
        if (status != null) {
            jobs = jobRepository.findByStatus(status, pageable);
        } else {
            jobs = jobRepository.findAll(pageable);
        }

        return jobs.map(this::mapToJobResponseDto);
    }

    public Page<JobResponseDto> getCurrentUserJobs(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Page<Job> jobs = jobRepository.findByClientId(client.getId(), pageable);

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