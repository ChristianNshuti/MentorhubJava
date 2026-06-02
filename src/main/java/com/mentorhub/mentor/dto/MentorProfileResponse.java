package com.mentorhub.mentor.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MentorProfileResponse {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private Integer experienceYears;
    private BigDecimal hourlyRate;
    private List<String> expertise;
    private boolean verified;
    private Double averageRating;
}
