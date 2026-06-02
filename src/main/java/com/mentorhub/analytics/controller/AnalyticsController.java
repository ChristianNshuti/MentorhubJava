package com.mentorhub.analytics.controller;

import com.mentorhub.analytics.dto.MentorAnalytics;
import com.mentorhub.analytics.dto.PlatformAnalytics;
import com.mentorhub.analytics.service.AnalyticsService;
import com.mentorhub.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/platform")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PlatformAnalytics> platform() {
        return ApiResponse.ok(analyticsService.platformStats());
    }

    @GetMapping("/mentor/me")
    public ApiResponse<MentorAnalytics> mentorMe() {
        return ApiResponse.ok(analyticsService.mentorStats());
    }
}
