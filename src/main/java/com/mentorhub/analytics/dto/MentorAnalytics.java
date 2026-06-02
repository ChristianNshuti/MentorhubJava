package com.mentorhub.analytics.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MentorAnalytics {
    private long sessionsCompleted;
    private Double averageRating;
    private BigDecimal estimatedEarnings;
}
