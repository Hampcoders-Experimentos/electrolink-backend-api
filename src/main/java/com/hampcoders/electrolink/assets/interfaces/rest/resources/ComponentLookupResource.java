package com.hampcoders.electrolink.assets.interfaces.rest.resources;

/**
 * Resource for simple component lookup (ID and name).
 */
public record ComponentLookupResource(Long id, String name) {}