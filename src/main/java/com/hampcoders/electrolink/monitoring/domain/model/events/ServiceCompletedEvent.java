package com.hampcoders.electrolink.monitoring.domain.model.events;

public record ServiceCompletedEvent(Long serviceOperationId, Long requestId, Long technicianId) {
}
