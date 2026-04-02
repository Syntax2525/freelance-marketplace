package com.freelance.dto;

import java.util.List;

public class AdminOverviewDto {

    private List<UserResponseDto> users;
    private List<JobResponseDto> jobs;
    private long userCount;
    private long jobCount;

    public AdminOverviewDto() {
    }

    public List<UserResponseDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponseDto> users) {
        this.users = users;
    }

    public List<JobResponseDto> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobResponseDto> jobs) {
        this.jobs = jobs;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getJobCount() {
        return jobCount;
    }

    public void setJobCount(long jobCount) {
        this.jobCount = jobCount;
    }
}
