package com.hampcoders.electrolink.assets.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resource used for updating a property's address and location details.
 */
public record UpdatePropertyResource(
    @NotNull @Valid AddressResource address,
    @NotBlank String region,
    @NotBlank String district
) {
}