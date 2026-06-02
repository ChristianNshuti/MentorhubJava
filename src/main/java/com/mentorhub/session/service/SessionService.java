package com.mentorhub.session.service;

import com.mentorhub.auth.security.SecurityUtils;
import com.mentorhub.common.exception.BadRequestException;
import com.mentorhub.common.exception.ResourceNotFoundException;
import com.mentorhub.mentor.repository.MentorProfileRepository;
import com.mentorhub.notification.entity.NotificationType;
import com.mentorhub.notification.service.NotificationService;
import com.mentorhub.session.dto.BookSessionRequest;
import com.mentorhub.session.entity.MentoringSession;
import com.mentorhub.session.entity.SessionStatus;
import com.mentorhub.session.repository.SessionRepository;
import com.mentorhub.user.entity.Role;
import com.mentorhub.user.entity.User;
import com.mentorhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final NotificationService notificationService;

    @Transactional
    public MentoringSession book(BookSessionRequest request) {
        if (SecurityUtils.currentUser().getRole() != Role.STUDENT) {
            throw new BadRequestException("Only students can book sessions");
        }
        User student = userRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        User mentor = userRepository.findById(request.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));
        if (mentor.getRole() != Role.MENTOR) {
            throw new BadRequestException("Target user is not a mentor");
        }
        mentorProfileRepository.findByUserId(mentor.getId())
                .filter(mp -> mp.isVerified())
                .orElseThrow(() -> new BadRequestException("Mentor is not verified"));

        MentoringSession session = MentoringSession.builder()
                .student(student)
                .mentor(mentor)
                .scheduledAt(request.getScheduledAt())
                .durationMinutes(request.getDurationMinutes())
                .status(SessionStatus.PENDING)
                .build();
        session = sessionRepository.save(session);
        notificationService.notify(mentor, NotificationType.SESSION_BOOKED,
                "New session request", student.getFirstName() + " requested a mentoring session");
        return session;
    }

    @Transactional
    public MentoringSession respond(Long sessionId, boolean accept) {
        MentoringSession session = getSessionForMentor(sessionId);
        session.setStatus(accept ? SessionStatus.ACCEPTED : SessionStatus.REJECTED);
        if (accept) {
            session.setMeetingUrl("https://meet.jit.si/mentorhub-" + UUID.randomUUID());
        }
        session = sessionRepository.save(session);
        notificationService.notify(session.getStudent(),
                accept ? NotificationType.SESSION_ACCEPTED : NotificationType.SESSION_REJECTED,
                accept ? "Session accepted" : "Session declined",
                "Your session on " + session.getScheduledAt() + " was " + (accept ? "accepted" : "declined"));
        return session;
    }

    @Transactional
    public MentoringSession complete(Long sessionId) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        Long userId = SecurityUtils.currentUserId();
        if (!session.getMentor().getId().equals(userId) && !session.getStudent().getId().equals(userId)) {
            throw new BadRequestException("Not allowed");
        }
        session.setStatus(SessionStatus.COMPLETED);
        return sessionRepository.save(session);
    }

    public List<MentoringSession> mySessions() {
        Long userId = SecurityUtils.currentUserId();
        if (SecurityUtils.currentUser().getRole() == Role.MENTOR) {
            return sessionRepository.findByMentorId(userId);
        }
        return sessionRepository.findByStudentId(userId);
    }

    private MentoringSession getSessionForMentor(Long sessionId) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        if (!session.getMentor().getId().equals(SecurityUtils.currentUserId())) {
            throw new BadRequestException("Not your session");
        }
        return session;
    }
}
