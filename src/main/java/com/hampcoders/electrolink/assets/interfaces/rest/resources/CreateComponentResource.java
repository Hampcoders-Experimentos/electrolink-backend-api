package com.hampcoders.electrolink.assets.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resource used for creating a new Component.
 */
public record CreateComponentResource(
    @NotBlank
    String name,
    String description,
    @NotNull
    @Min(1)
    Long componentTypeId,
    Boolean isActive
) {
}