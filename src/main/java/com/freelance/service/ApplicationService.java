package com.freelance.service;

import com.freelance.dto.ApplicationCreateDto;
import com.freelance.dto.ApplicationResponseDto;
import com.freelance.model.Application;
import com.freelance.model.Job;
import com.freelance.model.User;
import com.freelance.repository.ApplicationRepository;
import com.freelance.repository.JobRepository;
import com.freelance.repository.UserRepository;
import com.freelance.util.RoleGuard;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * Submit application for a job (FREELANCER only)
     */
    public ApplicationResponseDto submitApplication(Long jobId, ApplicationCreateDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User freelancer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        // Ensure only FREELANCER can apply for jobs
        RoleGuard.requireRole(freelancer, RoleGuard.ROLE_FREELANCER);
        RoleGuard.forbidClient(freelancer);
        RoleGuard.forbidAdmin(freelancer);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found with id: " + jobId));

        // Prevent freelancer from applying to own job
        if (job.getClient().getId().equals(freelancer.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "You cannot apply for your own job");
        }

        // Prevent applying to non-open jobs
        if (!job.getStatus().equals(Job.JobStatus.OPEN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot apply to a closed or in-progress job");
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

    /**
     * Get applications for a job (CLIENT who owns the job or ADMIN)
     */
    @Transactional(readOnly = true)
    public List<ApplicationResponseDto> getApplicationsForJob(Long jobId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        // Only CLIENT who owns the job or ADMIN can view applications
        if (RoleGuard.isClient(user)) {
            if (!job.getClient().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                        "You can only view applications for your own jobs");
            }
        } else if (!RoleGuard.isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Only job client or admin can view applications");
        }

        List<Application> applications = applicationRepository.findByJobId(jobId);
        return applications.stream()
                .map(this::mapToApplicationResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get current user's applications (FREELANCER only)
     */
    @Transactional(readOnly = true)
    public List<ApplicationResponseDto> getCurrentUserApplications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User freelancer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        // Ensure only FREELANCER can view their own applications
        RoleGuard.requireRole(freelancer, RoleGuard.ROLE_FREELANCER);

        List<Application> applications = applicationRepository.findByFreelancerId(freelancer.getId());
        return applications.stream()
                .map(this::mapToApplicationResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Accept an application (CLIENT who owns the job or ADMIN)
     */
    public ApplicationResponseDto acceptApplication(Long applicationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        // Only CLIENT who owns the job or ADMIN can accept
        if (RoleGuard.isClient(user)) {
            if (!application.getJob().getClient().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                        "You can only accept applications for your own jobs");
            }
        } else if (!RoleGuard.isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Only job client or admin can accept applications");
        }

        application.setStatus(Application.ApplicationStatus.ACCEPTED);
        Application updated = applicationRepository.save(application);
        return mapToApplicationResponseDto(updated);
    }

    /**
     * Reject an application (CLIENT who owns the job or ADMIN)
     */
    public ApplicationResponseDto rejectApplication(Long applicationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        // Only CLIENT who owns the job or ADMIN can reject
        if (RoleGuard.isClient(user)) {
            if (!application.getJob().getClient().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                        "You can only reject applications for your own jobs");
            }
        } else if (!RoleGuard.isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Only job client or admin can reject applications");
        }

        application.setStatus(Application.ApplicationStatus.REJECTED);
        Application updated = applicationRepository.save(application);
        return mapToApplicationResponseDto(updated);
    }

    /**
     * Withdraw an application (FREELANCER who submitted it)
     */
    public ApplicationResponseDto withdrawApplication(Long applicationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User freelancer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        // Ensure only FREELANCER can withdraw
        RoleGuard.requireRole(freelancer, RoleGuard.ROLE_FREELANCER);

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        // Verify freelancer owns this application
        if (!application.getFreelancer().getId().equals(freelancer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "You can only withdraw your own applications");
        }

        // Prevent withdrawing already accepted applications
        if (application.getStatus() == Application.ApplicationStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot withdraw an accepted application");
        }

        application.setStatus(Application.ApplicationStatus.WITHDRAWN);
        Application updated = applicationRepository.save(application);
        return mapToApplicationResponseDto(updated);
    }

    /**
     * Get single application details
     */
    @Transactional(readOnly = true)
    public ApplicationResponseDto getApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        return mapToApplicationResponseDto(application);
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