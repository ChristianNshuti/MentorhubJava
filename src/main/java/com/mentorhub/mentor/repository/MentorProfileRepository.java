package com.mentorhub.mentor.repository;

import com.mentorhub.mentor.entity.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {
    Optional<MentorProfile> findByUserId(Long userId);

    List<MentorProfile> findByVerifiedTrue();

    @Query("SELECT m FROM MentorProfile m WHERE m.verified = true AND " +
            "(:expertise IS NULL OR :expertise MEMBER OF m.expertise)")
    List<MentorProfile> searchVerified(String expertise);

    long countByVerifiedTrue();
}
