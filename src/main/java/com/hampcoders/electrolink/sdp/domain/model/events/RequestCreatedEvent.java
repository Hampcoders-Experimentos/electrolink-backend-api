package com.hampcoders.electrolink.sdp.domain.model.events;

public record RequestCreatedEvent(Long requestId, Long clientId, String serviceId,
                                  boolean isPriority, String propertyId) {
}
