// com.freelance.repository.ReviewRepository.java
package com.freelance.repository;

import com.freelance.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByReviewedUserId(Long reviewedUserId);

    Optional<Review> findByJobIdAndReviewerId(Long jobId, Long reviewerId);

    // For calculating average rating later in service layer
    // @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUser.id = :userId")
    // Double getAverageRatingByReviewedUserId(@Param("userId") Long userId);

}