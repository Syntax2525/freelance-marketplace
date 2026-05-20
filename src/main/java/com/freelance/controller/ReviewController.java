package com.freelance.controller;

import com.freelance.dto.ReviewCreateDto;
import com.freelance.dto.ReviewResponseDto;
import com.freelance.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Create a review (CLIENT reviews FREELANCER or FREELANCER reviews CLIENT after job completion)
     * Authenticated users only - role validation in service layer
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewCreateDto dto) {
        ReviewResponseDto review = reviewService.createReview(dto);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    /**
     * Get reviews for a user (publicly accessible)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForUser(@PathVariable Long userId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsForUser(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get reviews written by a user
     */
    @GetMapping("/written-by/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsWrittenByUser(@PathVariable Long userId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsWrittenByUser(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Delete a review (REVIEWER who wrote it, ADMIN access)
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
