package com.freelance.controller;

import com.freelance.config.SkillCatalog;
import com.freelance.dto.JobCreateDto;
import com.freelance.service.JobService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class JobViewController {

    private final JobService jobService;
    private final SkillCatalog skillCatalog;

    public JobViewController(JobService jobService, SkillCatalog skillCatalog) {
        this.jobService = jobService;
        this.skillCatalog = skillCatalog;
    }

    @PostMapping("/jobs/create")
    public String createJob(@Valid JobCreateDto jobForm,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("jobForm", jobForm);
            model.addAttribute("skills", skillCatalog.getAllSkillNames());
            return "create_job";
        }
        jobService.createJob(jobForm);
        return "redirect:/jobs";
    }
}
