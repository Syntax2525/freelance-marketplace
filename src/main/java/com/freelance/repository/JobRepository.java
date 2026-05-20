// com.freelance.repository.JobRepository.java
package com.freelance.repository;

import com.freelance.model.Job;
import com.freelance.model.Job.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByClientId(Long clientId, Pageable pageable);

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    Page<Job> findByCategory(String category, Pageable pageable);

    // Common search/filter patterns (you can extend later)
    // Page<Job> findByCategoryAndStatusAndBudgetTzsBetween(
    //         String category, JobStatus status, BigDecimal minBudget, BigDecimal maxBudget, Pageable pageable);

}
