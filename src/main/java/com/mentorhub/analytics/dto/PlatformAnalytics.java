package com.mentorhub.analytics.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformAnalytics {
    private long totalUsers;
    private long activeMentors;
    private long totalBookings;
    private long completedSessions;
}
