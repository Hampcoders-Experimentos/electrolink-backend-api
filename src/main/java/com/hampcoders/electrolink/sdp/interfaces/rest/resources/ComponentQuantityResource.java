package com.hampcoders.electrolink.sdp.interfaces.rest.resources;

/**
 * Resource representing a component and its quantity.
 *
 * @param componentId The ID of the component.
 *
 * @param quantity The quantity of the component.
 *
 */
public record ComponentQuantityResource(
        String componentId,
        int quantity
) {}
