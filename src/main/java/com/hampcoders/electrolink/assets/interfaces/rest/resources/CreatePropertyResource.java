package com.hampcoders.electrolink.assets.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resource used for creating a new Property.
 */
public record CreatePropertyResource(
    @NotBlank String ownerId,
    @NotNull @Valid AddressResource address,
    @NotBlank String region,
    @NotBlank String district
) {
}