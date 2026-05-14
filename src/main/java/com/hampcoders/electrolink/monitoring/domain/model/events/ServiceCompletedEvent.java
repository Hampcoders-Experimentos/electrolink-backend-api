package com.hampcoders.electrolink.monitoring.domain.model.events;

/**
 * Event representing the completion of a service operation.
 *
 * @param serviceOperationId The ID of the completed service operation.
 * @param requestId The ID of the associated service request.
 * @param technicianId The ID of the technician who completed the service.
 */
public record ServiceCompletedEvent(Long serviceOperationId, Long requestId, Long technicianId) {
}
