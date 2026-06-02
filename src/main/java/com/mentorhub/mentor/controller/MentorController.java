package com.mentorhub.mentor.controller;

import com.mentorhub.common.dto.ApiResponse;
import com.mentorhub.mentor.dto.MentorProfileResponse;
import com.mentorhub.mentor.dto.UpdateMentorProfileRequest;
import com.mentorhub.mentor.entity.MentorAvailability;
import com.mentorhub.mentor.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @GetMapping
    public ApiResponse<List<MentorProfileResponse>> search(
            @RequestParam(required = false) String expertise) {
        return ApiResponse.ok(mentorService.searchMentors(expertise));
    }

    @GetMapping("/{mentorUserId}")
    public ApiResponse<MentorProfileResponse> get(@PathVariable Long mentorUserId) {
        return ApiResponse.ok(mentorService.getMentor(mentorUserId));
    }

    @PutMapping("/me")
    public ApiResponse<MentorProfileResponse> updateProfile(@Valid @RequestBody UpdateMentorProfileRequest request) {
        return ApiResponse.ok(mentorService.updateMyProfile(request));
    }

    @PostMapping("/me/availability")
    public ApiResponse<MentorAvailability> addAvailability(@RequestBody Map<String, String> body) {
        DayOfWeek day = DayOfWeek.valueOf(body.get("dayOfWeek"));
        LocalTime start = LocalTime.parse(body.get("startTime"));
        LocalTime end = LocalTime.parse(body.get("endTime"));
        return ApiResponse.ok(mentorService.addAvailability(day, start, end));
    }

    @GetMapping("/{mentorUserId}/availability")
    public ApiResponse<List<MentorAvailability>> availability(@PathVariable Long mentorUserId) {
        return ApiResponse.ok(mentorService.getAvailability(mentorUserId));
    }
}
