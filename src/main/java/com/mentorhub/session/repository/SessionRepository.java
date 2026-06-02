package com.mentorhub.session.repository;

import com.mentorhub.session.entity.MentoringSession;
import com.mentorhub.session.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<MentoringSession, Long> {
    List<MentoringSession> findByStudentId(Long studentId);

    List<MentoringSession> findByMentorId(Long mentorId);

    long countByStatus(SessionStatus status);

    long countByMentorIdAndStatus(Long mentorId, SessionStatus status);
}
