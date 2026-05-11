package com.hampcoders.electrolink.analytics.interfaces.rest.resources;

public record TechnicianPerformanceResource(
    Long technicianId,
    int totalServicesCompleted,
    double averageRating,
    double averageCompletionTimeHours,
    int pendingServices
) {
}
