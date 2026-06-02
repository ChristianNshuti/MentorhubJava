package com.mentorhub.review.repository;

import com.mentorhub.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMentorId(Long mentorId);

    Optional<Review> findByStudentIdAndMentorId(Long studentId, Long mentorId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.mentor.id = :mentorId")
    Double averageRatingByMentorId(Long mentorId);
}
