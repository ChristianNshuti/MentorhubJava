package com.mentorhub.mentor.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateMentorProfileRequest {
    private String bio;
    private Integer experienceYears;
    private BigDecimal hourlyRate;
    private List<String> expertise;
    private String certificateUrl;
}
