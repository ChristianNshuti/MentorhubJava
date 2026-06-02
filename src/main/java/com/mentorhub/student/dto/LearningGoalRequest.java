package com.mentorhub.student.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class LearningGoalRequest {
    @NotBlank
    private String title;
    private String description;
    private List<String> milestoneTitles;
}
