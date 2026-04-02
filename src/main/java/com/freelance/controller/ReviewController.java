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

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewCreateDto dto) {
        ReviewResponseDto review = reviewService.createReview(dto);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForUser(@PathVariable Long userId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsForUser(userId);
        return ResponseEntity.ok(reviews);
    }
}
