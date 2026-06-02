package com.mentorhub.admin.service;

import com.mentorhub.admin.entity.Report;
import com.mentorhub.admin.repository.ReportRepository;
import com.mentorhub.common.exception.ResourceNotFoundException;
import com.mentorhub.mentor.entity.MentorProfile;
import com.mentorhub.mentor.repository.MentorProfileRepository;
import com.mentorhub.notification.entity.NotificationType;
import com.mentorhub.notification.service.NotificationService;
import com.mentorhub.user.entity.User;
import com.mentorhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final ReportRepository reportRepository;
    private final NotificationService notificationService;

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public MentorProfile verifyMentor(Long mentorUserId, boolean approve) {
        MentorProfile profile = mentorProfileRepository.findByUserId(mentorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));
        profile.setVerified(approve);
        profile = mentorProfileRepository.save(profile);
        if (approve) {
            notificationService.notify(profile.getUser(), NotificationType.MENTOR_VERIFIED,
                    "Profile verified", "Your mentor profile has been approved by admin");
        }
        return profile;
    }

    public List<Report> openReports() {
        return reportRepository.findByStatus("OPEN");
    }

    @Transactional
    public Report resolveReport(Long reportId, String status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setStatus(status);
        return reportRepository.save(report);
    }
}
