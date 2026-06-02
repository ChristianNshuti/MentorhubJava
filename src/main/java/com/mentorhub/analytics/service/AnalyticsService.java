package com.mentorhub.analytics.service;

import com.mentorhub.analytics.dto.MentorAnalytics;
import com.mentorhub.analytics.dto.PlatformAnalytics;
import com.mentorhub.auth.security.SecurityUtils;
import com.mentorhub.common.exception.BadRequestException;
import com.mentorhub.mentor.entity.MentorProfile;
import com.mentorhub.mentor.repository.MentorProfileRepository;
import com.mentorhub.review.repository.ReviewRepository;
import com.mentorhub.session.entity.SessionStatus;
import com.mentorhub.session.repository.SessionRepository;
import com.mentorhub.user.entity.Role;
import com.mentorhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final SessionRepository sessionRepository;
    private final ReviewRepository reviewRepository;

    public PlatformAnalytics platformStats() {
        return PlatformAnalytics.builder()
                .totalUsers(userRepository.count())
                .activeMentors(mentorProfileRepository.countByVerifiedTrue())
                .totalBookings(sessionRepository.count())
                .completedSessions(sessionRepository.countByStatus(SessionStatus.COMPLETED))
                .build();
    }

    public MentorAnalytics mentorStats() {
        if (SecurityUtils.currentUser().getRole() != Role.MENTOR) {
            throw new BadRequestException("Mentor analytics only");
        }
        Long mentorId = SecurityUtils.currentUserId();
        long completed = sessionRepository.countByMentorIdAndStatus(mentorId, SessionStatus.COMPLETED);
        Double avgRating = reviewRepository.averageRatingByMentorId(mentorId);
        MentorProfile profile = mentorProfileRepository.findByUserId(mentorId).orElse(null);
        BigDecimal earnings = BigDecimal.ZERO;
        if (profile != null && profile.getHourlyRate() != null) {
            earnings = profile.getHourlyRate().multiply(BigDecimal.valueOf(completed));
        }
        return MentorAnalytics.builder()
                .sessionsCompleted(completed)
                .averageRating(avgRating)
                .estimatedEarnings(earnings)
                .build();
    }
}
