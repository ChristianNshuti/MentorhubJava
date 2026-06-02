package com.mentorhub.student.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateStudentProfileRequest {
    private String school;
    private List<String> interests;
}
