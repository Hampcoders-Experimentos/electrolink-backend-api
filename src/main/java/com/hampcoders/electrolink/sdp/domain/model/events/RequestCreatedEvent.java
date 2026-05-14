package com.hampcoders.electrolink.sdp.domain.model.events;

/**
 * Event representing the creation of a new service request.
 *
 * @param requestId Unique identifier for the service request.
 * @param clientId Identifier for the client who created the request.
 * @param serviceId Identifier for the type of service requested.
 * @param isPriority Flag indicating if the request is a priority.
 * @param propertyId Identifier for the property associated with the request.
 */
public record RequestCreatedEvent(Long requestId, Long clientId, String serviceId,
                                  boolean isPriority, String propertyId) {
}
