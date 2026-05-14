package com.hampcoders.electrolink.sdp.domain.model.events;

/**
 * Event representing the assignment of a service request to a technician.
 *
 * @param requestId The unique identifier of the assigned service request.
 * @param technicianId The unique identifier of the assigned technician.
 */
public record ServiceAssignedEvent(Long requestId, Long technicianId) {
}
