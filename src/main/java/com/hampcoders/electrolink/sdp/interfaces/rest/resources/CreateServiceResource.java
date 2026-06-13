package com.hampcoders.electrolink.sdp.interfaces.rest.resources;

import java.util.List;

/**
 * Resource representing a request to create a new service.
 *
 * @param name          The name of the service.
 *
 * @param description   A description of the service.
 *
 * @param basePrice     The base price of the service.
 *
 * @param estimatedTime The estimated time to complete the service.
 *
 * @param category      The category of the service.
 *
 * @param isVisible     Whether the service is visible to clients.
 *
 * @param createdBy     The ID of the user who created the service.
 *
 * @param policy        The policy associated with the service.
 *
 * @param restriction   The restriction associated with the service.
 *
 * @param tags          A list of tags associated with the service.
 *
 * @param components    A list of components required for the service along with their quantities.
 */
public record CreateServiceResource(
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
