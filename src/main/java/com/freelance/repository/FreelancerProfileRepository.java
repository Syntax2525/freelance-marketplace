// com.freelance.repository.FreelancerProfileRepository.java
package com.freelance.repository;

import com.freelance.model.FreelancerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreelancerProfileRepository extends JpaRepository<FreelancerProfile, Long> {

    // Because FreelancerProfile has 1:1 with User and shared PK
    Optional<FreelancerProfile> findByUserId(Long userId);

}
