package com.mentorhub.session.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookSessionRequest {
    @NotNull
    private Long mentorId;
    @NotNull
    @Future
    private LocalDateTime scheduledAt;
    @Min(15)
    private int durationMinutes = 60;
}
