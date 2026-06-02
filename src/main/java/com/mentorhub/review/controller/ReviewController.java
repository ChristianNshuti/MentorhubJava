package com.mentorhub.review.controller;

import com.mentorhub.common.dto.ApiResponse;
import com.mentorhub.review.dto.CreateReviewRequest;
import com.mentorhub.review.entity.Review;
import com.mentorhub.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<Review> create(@Valid @RequestBody CreateReviewRequest request) {
        return ApiResponse.ok(reviewService.create(request));
    }

    @GetMapping("/mentor/{mentorId}")
    public ApiResponse<List<Review>> forMentor(@PathVariable Long mentorId) {
        return ApiResponse.ok(reviewService.forMentor(mentorId));
    }
}
