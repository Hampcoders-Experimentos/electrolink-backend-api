package com.hampcoders.electrolink.assets.interfaces.rest.resources;

/**
 * Resource representing a component type with its basic details.
 */
public record ComponentTypeResource(Long componentTypeId, String name, String description) {
}