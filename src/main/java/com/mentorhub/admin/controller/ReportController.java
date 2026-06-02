package com.mentorhub.admin.controller;

import com.mentorhub.admin.dto.CreateReportRequest;
import com.mentorhub.admin.entity.Report;
import com.mentorhub.admin.repository.ReportRepository;
import com.mentorhub.auth.security.SecurityUtils;
import com.mentorhub.common.dto.ApiResponse;
import com.mentorhub.user.entity.User;
import com.mentorhub.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ApiResponse<Report> submit(@Valid @RequestBody CreateReportRequest request) {
        User reporter = userRepository.findById(SecurityUtils.currentUserId()).orElseThrow();
        Report report = Report.builder()
                .reporter(reporter)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .build();
        return ApiResponse.ok(reportRepository.save(report));
    }
}
