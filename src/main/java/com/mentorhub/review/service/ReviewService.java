package com.mentorhub.review.service;

import com.mentorhub.auth.security.SecurityUtils;
import com.mentorhub.common.exception.BadRequestException;
import com.mentorhub.common.exception.ResourceNotFoundException;
import com.mentorhub.review.dto.CreateReviewRequest;
import com.mentorhub.review.entity.Review;
import com.mentorhub.review.repository.ReviewRepository;
import com.mentorhub.user.entity.Role;
import com.mentorhub.user.entity.User;
import com.mentorhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public Review create(CreateReviewRequest request) {
        if (SecurityUtils.currentUser().getRole() != Role.STUDENT) {
            throw new BadRequestException("Only students can leave reviews");
        }
        Long studentId = SecurityUtils.currentUserId();
        if (reviewRepository.findByStudentIdAndMentorId(studentId, request.getMentorId()).isPresent()) {
            throw new BadRequestException("You already reviewed this mentor");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        User mentor = userRepository.findById(request.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));
        return reviewRepository.save(Review.builder()
                .student(student)
                .mentor(mentor)
                .rating(request.getRating())
                .comment(request.getComment())
                .build());
    }

    public List<Review> forMentor(Long mentorId) {
        return reviewRepository.findByMentorId(mentorId);
    }
}
