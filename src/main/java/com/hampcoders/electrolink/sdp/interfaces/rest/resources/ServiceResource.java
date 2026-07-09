package com.hampcoders.electrolink.sdp.interfaces.rest.resources;

import java.util.List;

/**
 * ServiceResource is a record that represents the data structure for a service in the system.
 *
 * @param id The unique identifier of the service.
 * @param name The name of the service.
 * @param description A brief description of the service.
 * @param basePrice The base price of the service.
 * @param estimatedTime The estimated time to complete the service.
 * @param category The category to which the service belongs.
 * @param isVisible A flag indicating whether the service is visible to customers.
 * @param createdBy The username of the user who created the service.
 * @param policy The policy associated with the service.
 * @param restriction The restriction associated with the service.
 * @param tags A list of tags associated with the service.
 * @param components A list of components and their quantities required for the service.
 */
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
