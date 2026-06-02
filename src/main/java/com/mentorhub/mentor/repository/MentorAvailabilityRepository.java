package com.mentorhub.mentor.repository;

import com.mentorhub.mentor.entity.MentorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorAvailabilityRepository extends JpaRepository<MentorAvailability, Long> {
    List<MentorAvailability> findByMentorProfileId(Long mentorProfileId);
}
