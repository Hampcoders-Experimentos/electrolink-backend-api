package com.hampcoders.electrolink.sdp.domain.model.events;

/**
 * Event representing the cataloguing of a service in the Service Delivery Platform (SDP).
 *
 * @param serviceId The unique identifier of the catalogued service.
 * @param createdBy The identifier of the user or system that catalogued the service.
 * @param name The name of the catalogued service.
 */
public record ServiceCataloguedEvent(Long serviceId, String createdBy, String name) {
}
