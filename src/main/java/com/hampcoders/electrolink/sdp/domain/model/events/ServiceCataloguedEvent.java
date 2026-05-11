package com.hampcoders.electrolink.sdp.domain.model.events;

public record ServiceCataloguedEvent(Long serviceId, String createdBy, String name) {
}
