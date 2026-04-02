package com.freelance.service;

import com.freelance.dto.ApplicationCreateDto;
import com.freelance.dto.ApplicationResponseDto;
import com.freelance.model.Application;
import com.freelance.model.Job;
import com.freelance.model.User;
import com.freelance.repository.ApplicationRepository;
import com.freelance.repository.JobRepository;
import com.freelance.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public ApplicationResponseDto submitApplication(Long jobId, ApplicationCreateDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User freelancer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated freelancer not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        if (!job.getStatus().equals(Job.JobStatus.OPEN)) {
            throw new RuntimeException("Cannot apply to a closed or in-progress job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setFreelancer(freelancer);
        application.setCoverLetter(dto.getCoverLetter());
        application.setProposedAmountTzs(dto.getProposedAmountTzs());
        application.setProposedTimeline(dto.getProposedTimeline());
        application.setStatus(Application.ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());

        Application saved = applicationRepository.save(application);

        return mapToApplicationResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponseDto> getApplicationsForJob(Long jobId) {
        List<Application> applications = applicationRepository.findByJobId(jobId);
        return applications.stream()
                .map(this::mapToApplicationResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponseDto> getCurrentUserApplications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User freelancer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        List<Application> applications = applicationRepository.findByFreelancerId(freelancer.getId());
        return applications.stream()
                .map(this::mapToApplicationResponseDto)
                .collect(Collectors.toList());
    }

    private ApplicationResponseDto mapToApplicationResponseDto(Application app) {
        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(app.getId());
        dto.setJobId(app.getJob().getId());
        dto.setJobTitle(app.getJob().getTitle());
        dto.setClientName(app.getJob().getClient().getFullName());
        dto.setFreelancerId(app.getFreelancer().getId());
        dto.setFreelancerName(app.getFreelancer().getFullName());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setProposedAmountTzs(app.getProposedAmountTzs());
        dto.setProposedTimeline(app.getProposedTimeline());
        dto.setStatus(app.getStatus());
        dto.setAppliedAt(app.getAppliedAt());
        return dto;
    }
}