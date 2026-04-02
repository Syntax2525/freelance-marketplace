// com.freelance.repository.ApplicationRepository.java
package com.freelance.repository;

import com.freelance.model.Application;
import com.freelance.model.Application.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobId(Long jobId);

    List<Application> findByFreelancerId(Long freelancerId);

    Optional<Application> findByJobIdAndFreelancerId(Long jobId, Long freelancerId);

    List<Application> findByStatus(ApplicationStatus status);

}