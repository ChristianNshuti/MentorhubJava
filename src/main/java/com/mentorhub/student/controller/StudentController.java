package com.mentorhub.student.controller;

import com.mentorhub.common.dto.ApiResponse;
import com.mentorhub.student.dto.LearningGoalRequest;
import com.mentorhub.student.dto.UpdateStudentProfileRequest;
import com.mentorhub.student.entity.LearningGoal;
import com.mentorhub.student.entity.StudentProfile;
import com.mentorhub.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/me")
    public ApiResponse<StudentProfile> myProfile() {
        return ApiResponse.ok(studentService.getMyProfile());
    }

    @PutMapping("/me")
    public ApiResponse<StudentProfile> update(@Valid @RequestBody UpdateStudentProfileRequest request) {
        return ApiResponse.ok(studentService.updateProfile(request));
    }

    @GetMapping("/me/goals")
    public ApiResponse<List<LearningGoal>> goals() {
        return ApiResponse.ok(studentService.myGoals());
    }

    @PostMapping("/me/goals")
    public ApiResponse<LearningGoal> createGoal(@Valid @RequestBody LearningGoalRequest request) {
        return ApiResponse.ok(studentService.createGoal(request));
    }

    @PatchMapping("/me/goals/{goalId}/milestones/{milestoneId}/complete")
    public ApiResponse<LearningGoal> completeMilestone(
            @PathVariable Long goalId, @PathVariable Long milestoneId) {
        return ApiResponse.ok(studentService.completeMilestone(goalId, milestoneId));
    }
}
