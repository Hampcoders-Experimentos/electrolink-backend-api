package com.hampcoders.electrolink.sdp.interfaces.rest.resource;

import java.util.List;

public record ServiceResource(
    Long id,
    String name,
    String description,
    Double basePrice,
    String estimatedTime,
    String category,
    boolean isVisible,
    String createdBy,
    PolicyResource policy,
    RestrictionResource restriction,
    List<TagResource> tags,
    List<ComponentQuantityResource> components
) {}
