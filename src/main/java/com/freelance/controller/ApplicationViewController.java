package com.freelance.controller;

import com.freelance.dto.ApplicationCreateDto;
import com.freelance.repository.JobRepository;
import com.freelance.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ApplicationViewController {

    private final ApplicationService applicationService;
    private final JobRepository jobRepository;

    public ApplicationViewController(ApplicationService applicationService, JobRepository jobRepository) {
        this.applicationService = applicationService;
        this.jobRepository = jobRepository;
    }

    @PostMapping("/jobs/{jobId}/apply")
    public String apply(@PathVariable Long jobId,
                        @Valid ApplicationCreateDto applicationForm,
                        BindingResult bindingResult,
                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("applicationForm", applicationForm);
            model.addAttribute("jobs", jobRepository.findAll());
            return "job_listing";
        }
        applicationService.submitApplication(jobId, applicationForm);
        return "redirect:/jobs?applied";
    }
}
