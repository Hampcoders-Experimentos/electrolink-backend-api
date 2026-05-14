package com.hampcoders.electrolink.analytics.interfaces.rest.resources;

/**
 * Resource class representing the performance metrics of a technician.
 *
 * @param technicianId the unique identifier of the technician
 * @param totalServicesCompleted the total number of services completed by the technician
 * @param averageRating the average customer rating for the technician
 * @param averageCompletionTimeHours the average time taken to complete services in hours
 * @param pendingServices the number of services currently pending for the technician
 */
public record TechnicianPerformanceResource(
    Long technicianId,
    int totalServicesCompleted,
    double averageRating,
    double averageCompletionTimeHours,
    int pendingServices
) {
}
