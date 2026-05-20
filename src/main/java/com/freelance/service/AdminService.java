package com.freelance.service;

import com.freelance.dto.AdminOverviewDto;
import com.freelance.dto.JobResponseDto;
import com.freelance.dto.UserResponseDto;
import com.freelance.model.Job;
import com.freelance.model.User;
import com.freelance.repository.JobRepository;
import com.freelance.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public AdminService(UserRepository userRepository, JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public AdminOverviewDto getOverview() {
        AdminOverviewDto dto = new AdminOverviewDto();
        List<UserResponseDto> users = userRepository.findAll(PageRequest.of(0, 500)).stream()
                .map(this::mapUser)
                .collect(Collectors.toList());
        List<JobResponseDto> jobs = jobRepository.findAll(PageRequest.of(0, 500)).stream()
                .map(this::mapJob)
                .collect(Collectors.toList());
        dto.setUsers(users);
        dto.setJobs(jobs);
        dto.setUserCount(userRepository.count());
        dto.setJobCount(jobRepository.count());
        return dto;
    }

    private UserResponseDto mapUser(User user) {
        UserResponseDto d = new UserResponseDto();
        d.setId(user.getId());
        d.setEmail(user.getEmail());
        d.setFullName(user.getFullName());
        d.setPhone(user.getPhone());
        d.setCity(user.getCity());
        d.setRegion(user.getRegion());
        d.setBio(user.getBio());
        d.setProfilePictureUrl(user.getProfilePictureUrl());
        d.setActive(user.isActive());
        d.setEmailVerified(user.isEmailVerified());
        Set<String> roleNames = new HashSet<>();
        roleNames.add(user.getRoleName());
        d.setRoles(roleNames);
        d.setCreatedAt(user.getCreatedAt());
        d.setUpdatedAt(user.getUpdatedAt());
        return d;
    }

    private JobResponseDto mapJob(Job job) {
        JobResponseDto d = new JobResponseDto();
        d.setId(job.getId());
        d.setClientId(job.getClient().getId());
        d.setClientName(job.getClient().getFullName());
        d.setTitle(job.getTitle());
        d.setDescription(job.getDescription());
        d.setCategory(job.getCategory());
        d.setBudgetTzs(job.getBudgetTzs());
        d.setBudgetType(job.getBudgetType());
        d.setLocationPreference(job.getLocationPreference());
        d.setStatus(job.getStatus());
        d.setCreatedAt(job.getCreatedAt());
        d.setUpdatedAt(job.getUpdatedAt());
        return d;
    }

	public Object getAllUsers(String role, Boolean active) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object updateUserStatus(Long userId, boolean active) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object changeUserRole(Long userId, String newRole) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteUser(Long userId) {
		// TODO Auto-generated method stub
		
	}

	public void deleteJob(Long jobId) {
		// TODO Auto-generated method stub
		
	}

	public Object getAllMessages(Long userId, int page, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object resolveDispute(Long jobId, String resolution) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getSystemReport() {
		// TODO Auto-generated method stub
		return null;
	}
}
