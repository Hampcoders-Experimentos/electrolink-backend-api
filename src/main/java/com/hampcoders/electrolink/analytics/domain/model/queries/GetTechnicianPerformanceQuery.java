package com.hampcoders.electrolink.analytics.domain.model.queries;

/**
 * Query object representing the data needed to retrieve the performance metrics of a technician.
 *
 * @param technicianId The ID of the technician whose performance metrics are being queried.
 */
public record GetTechnicianPerformanceQuery(Long technicianId) {
}
