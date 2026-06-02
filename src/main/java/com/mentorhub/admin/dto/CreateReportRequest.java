package com.mentorhub.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateReportRequest {
    @NotBlank
    private String targetType;
    private Long targetId;
    @NotBlank
    private String reason;
}
