package com.hampcoders.electrolink.monitoring.domain.model.queries;

/**
 * Query object representing the data needed to retrieve featured ratings
 * for a technician by their ID.
 *
 * @param technicianId The ID of the technician whose featured ratings are being queried.
 */
public record GetFeaturedRatingsByTechnicianIdQuery(Long technicianId) {
}
