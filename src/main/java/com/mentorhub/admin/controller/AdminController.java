package com.mentorhub.admin.controller;

import com.mentorhub.admin.entity.Report;
import com.mentorhub.admin.service.AdminService;
import com.mentorhub.common.dto.ApiResponse;
import com.mentorhub.mentor.entity.MentorProfile;
import com.mentorhub.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ApiResponse<List<User>> users() {
        return ApiResponse.ok(adminService.allUsers());
    }

    @PatchMapping("/mentors/{mentorUserId}/verify")
    public ApiResponse<MentorProfile> verify(
            @PathVariable Long mentorUserId, @RequestBody Map<String, Boolean> body) {
        return ApiResponse.ok(adminService.verifyMentor(mentorUserId, Boolean.TRUE.equals(body.get("approve"))));
    }

    @GetMapping("/reports")
    public ApiResponse<List<Report>> reports() {
        return ApiResponse.ok(adminService.openReports());
    }

    @PatchMapping("/reports/{id}")
    public ApiResponse<Report> resolve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ApiResponse.ok(adminService.resolveReport(id, body.getOrDefault("status", "RESOLVED")));
    }
}
