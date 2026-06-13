package com.hampcoders.electrolink.assets.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

/**
 * Resource used for initiating the creation of a new technician inventory.
 */
public record CreateTechnicianInventoryResource(
    @NotNull Long technicianId
) {}