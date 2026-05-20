package com.freelance.service;

import com.freelance.dto.ReviewCreateDto;
import com.freelance.dto.ReviewResponseDto;
import com.freelance.model.Application;
import com.freelance.model.Job;
import com.freelance.model.Review;
import com.freelance.model.User;
import com.freelance.repository.ApplicationRepository;
import com.freelance.repository.ReviewRepository;
import com.freelance.repository.UserRepository;
import com.freelance.util.RoleGuard;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ApplicationRepository applicationRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a review - CLIENT reviews FREELANCER or FREELANCER reviews CLIENT
     */
    public ReviewResponseDto createReview(ReviewCreateDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User reviewer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Reviewer not found"));

        // ADMIN cannot review jobs
        if (RoleGuard.isAdmin(reviewer)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Administrators cannot submit reviews");
        }

        Application application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        Job job = application.getJob();

        // Can only review completed jobs
        if (!job.getStatus().equals(Job.JobStatus.COMPLETED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Can only review completed jobs");
        }

        // Reviewer must be either the client or the freelancer of the job
        boolean isClient = job.getClient().getId().equals(reviewer.getId());
        boolean isFreelancer = application.getFreelancer().getId().equals(reviewer.getId());

        if (!isClient && !isFreelancer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "You are not associated with this job and cannot review it");
        }

        // Prevent reviewing self
        if (isClient && job.getClient().getId().equals(dto.getReviewedUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "You cannot review yourself");
        }
        if (isFreelancer && application.getFreelancer().getId().equals(dto.getReviewedUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "You cannot review yourself");
        }

        // Prevent duplicate reviews for the same job and reviewer
        boolean alreadyReviewed = reviewRepository.findByJobIdAndReviewerId(job.getId(), reviewer.getId())
                .isPresent();
        if (alreadyReviewed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "You have already reviewed this job");
        }

        Review review = new Review();
        review.setJob(job);
        review.setReviewer(reviewer);
        review.setReviewedUser(isClient ? application.getFreelancer() : job.getClient());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review saved = reviewRepository.save(review);

        return mapToReviewResponseDto(saved);
    }

    /**
     * Get reviews for a user (public - all authenticated users can view)
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsForUser(Long userId) {
        List<Review> reviews = reviewRepository.findByReviewedUserId(userId);
        return reviews.stream()
                .map(this::mapToReviewResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get reviews written by a user
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsWrittenByUser(Long userId) {
        List<Review> reviews = reviewRepository.findByReviewerId(userId);
        return reviews.stream()
                .map(this::mapToReviewResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Delete a review (reviewer who wrote it or ADMIN)
     */
    public void deleteReview(Long reviewId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        // Only reviewer or ADMIN can delete
        if (!review.getReviewer().getId().equals(user.getId()) && !RoleGuard.isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "You can only delete your own reviews");
        }

        reviewRepository.deleteById(reviewId);
    }

    private List<ReviewResponseDto> mapToReviewResponseDtoList(List<Review> reviews) {
        return reviews.stream()
                .map(this::mapToReviewResponseDto)
                .collect(Collectors.toList());
    }

    private ReviewResponseDto mapToReviewResponseDto(Review r) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(r.getId());
        dto.setJobId(r.getJob().getId());
        dto.setReviewerId(r.getReviewer().getId());
        dto.setReviewerName(r.getReviewer().getFullName());
        dto.setReviewedUserId(r.getReviewedUser().getId());
        dto.setReviewedUserName(r.getReviewedUser().getFullName());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        return dto;
    }
}