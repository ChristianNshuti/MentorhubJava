package com.mentorhub.mentor.service;

import com.mentorhub.auth.security.SecurityUtils;
import com.mentorhub.common.exception.BadRequestException;
import com.mentorhub.common.exception.ResourceNotFoundException;
import com.mentorhub.mentor.dto.MentorProfileResponse;
import com.mentorhub.mentor.dto.UpdateMentorProfileRequest;
import com.mentorhub.mentor.entity.MentorAvailability;
import com.mentorhub.mentor.entity.MentorProfile;
import com.mentorhub.mentor.mapper.MentorMapper;
import com.mentorhub.mentor.repository.MentorAvailabilityRepository;
import com.mentorhub.mentor.repository.MentorProfileRepository;
import com.mentorhub.review.repository.ReviewRepository;
import com.mentorhub.user.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorProfileRepository mentorProfileRepository;
    private final MentorAvailabilityRepository availabilityRepository;
    private final ReviewRepository reviewRepository;
    private final MentorMapper mentorMapper;

    public List<MentorProfileResponse> searchMentors(String expertise) {
        List<MentorProfile> mentors = expertise == null || expertise.isBlank()
                ? mentorProfileRepository.findByVerifiedTrue()
                : mentorProfileRepository.searchVerified(expertise);
        return mentors.stream().map(this::toResponseWithRating).toList();
    }

    public MentorProfileResponse getMentor(Long mentorUserId) {
        MentorProfile profile = mentorProfileRepository.findByUserId(mentorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));
        return toResponseWithRating(profile);
    }

    @Transactional
    public MentorProfileResponse updateMyProfile(UpdateMentorProfileRequest request) {
        MentorProfile profile = getMyProfileEntity();
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getExperienceYears() != null) profile.setExperienceYears(request.getExperienceYears());
        if (request.getHourlyRate() != null) profile.setHourlyRate(request.getHourlyRate());
        if (request.getExpertise() != null) profile.setExpertise(request.getExpertise());
        if (request.getCertificateUrl() != null) profile.setCertificateUrl(request.getCertificateUrl());
        return toResponseWithRating(mentorProfileRepository.save(profile));
    }

    @Transactional
    public MentorAvailability addAvailability(DayOfWeek day, LocalTime start, LocalTime end) {
        MentorProfile profile = getMyProfileEntity();
        return availabilityRepository.save(MentorAvailability.builder()
                .mentorProfile(profile)
                .dayOfWeek(day)
                .startTime(start)
                .endTime(end)
                .build());
    }

    public List<MentorAvailability> getAvailability(Long mentorUserId) {
        MentorProfile profile = mentorProfileRepository.findByUserId(mentorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));
        return availabilityRepository.findByMentorProfileId(profile.getId());
    }

    public MentorProfile getMyProfileEntity() {
        if (SecurityUtils.currentUser().getRole() != Role.MENTOR) {
            throw new BadRequestException("Only mentors can access mentor profile");
        }
        return mentorProfileRepository.findByUserId(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor profile not found"));
    }

    private MentorProfileResponse toResponseWithRating(MentorProfile profile) {
        MentorProfileResponse response = mentorMapper.toResponse(profile);
        response.setAverageRating(reviewRepository.averageRatingByMentorId(profile.getUser().getId()));
        return response;
    }
}
