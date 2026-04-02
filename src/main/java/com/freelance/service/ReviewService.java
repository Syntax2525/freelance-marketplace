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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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

    public ReviewResponseDto createReview(ReviewCreateDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User reviewer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        Application application = applicationRepository.findById(dto.getJobId())
                .orElseThrow(() -> new RuntimeException("Application/job not found"));

        Job job = application.getJob();

        if (!job.getStatus().equals(Job.JobStatus.COMPLETED)) {
            throw new RuntimeException("Can only review completed jobs");
        }

        // Basic check: only client can review freelancer or vice versa
        if (!job.getClient().equals(reviewer) && !application.getFreelancer().equals(reviewer)) {
            throw new RuntimeException("You are not authorized to review this job");
        }

        // Prevent duplicate reviews for the same job
        Optional<Review> existing = reviewRepository.findByJobIdAndReviewerId(job.getId(), reviewer.getId());
        if (existing.isPresent()) {
            throw new RuntimeException("You have already reviewed this job");
        }

        Review review = new Review();
        review.setJob(job);
        review.setReviewer(reviewer);
        review.setReviewedUser(job.getClient().equals(reviewer) ? application.getFreelancer() : job.getClient());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review saved = reviewRepository.save(review);

        return mapToReviewResponseDto(saved);
    }

    public List<ReviewResponseDto> getReviewsForUser(Long userId) {
        List<Review> reviews = reviewRepository.findByReviewedUserId(userId);
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