package com.mentorhub.student.service;

import com.mentorhub.auth.security.SecurityUtils;
import com.mentorhub.common.exception.BadRequestException;
import com.mentorhub.common.exception.ResourceNotFoundException;
import com.mentorhub.student.dto.LearningGoalRequest;
import com.mentorhub.student.dto.UpdateStudentProfileRequest;
import com.mentorhub.student.entity.LearningGoal;
import com.mentorhub.student.entity.Milestone;
import com.mentorhub.student.entity.StudentProfile;
import com.mentorhub.student.repository.LearningGoalRepository;
import com.mentorhub.student.repository.StudentProfileRepository;
import com.mentorhub.user.entity.Role;
import com.mentorhub.user.entity.User;
import com.mentorhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final LearningGoalRepository learningGoalRepository;
    private final UserRepository userRepository;

    public StudentProfile getMyProfile() {
        return getMyProfileEntity();
    }

    @Transactional
    public StudentProfile updateProfile(UpdateStudentProfileRequest request) {
        StudentProfile profile = getMyProfileEntity();
        if (request.getSchool() != null) profile.setSchool(request.getSchool());
        if (request.getInterests() != null) profile.setInterests(request.getInterests());
        return studentProfileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public List<LearningGoal> myGoals() {
        if (SecurityUtils.currentUser().getRole() != Role.STUDENT) {
            throw new BadRequestException("Only students can access learning goals");
        }
        return learningGoalRepository.findByStudentIdWithMilestones(SecurityUtils.currentUserId());
    }

    @Transactional
    public LearningGoal createGoal(LearningGoalRequest request) {
        User student = userRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        LearningGoal goal = LearningGoal.builder()
                .student(student)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        if (request.getMilestoneTitles() != null) {
            int order = 0;
            for (String title : request.getMilestoneTitles()) {
                goal.getMilestones().add(Milestone.builder()
                        .goal(goal)
                        .title(title)
                        .sortOrder(order++)
                        .build());
            }
        }
        return learningGoalRepository.save(goal);
    }

    @Transactional
    public LearningGoal completeMilestone(Long goalId, Long milestoneId) {
        LearningGoal goal = learningGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        if (!goal.getStudent().getId().equals(SecurityUtils.currentUserId())) {
            throw new BadRequestException("Not your goal");
        }
        goal.getMilestones().stream()
                .filter(m -> m.getId().equals(milestoneId))
                .findFirst()
                .ifPresent(m -> m.setCompleted(true));
        long completed = goal.getMilestones().stream().filter(Milestone::isCompleted).count();
        int total = goal.getMilestones().size();
        goal.setProgressPercent(total == 0 ? 0 : (int) ((completed * 100) / total));
        return learningGoalRepository.save(goal);
    }

    private StudentProfile getMyProfileEntity() {
        if (SecurityUtils.currentUser().getRole() != Role.STUDENT) {
            throw new BadRequestException("Only students can access student profile");
        }
        return studentProfileRepository.findByUserId(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
    }
}
