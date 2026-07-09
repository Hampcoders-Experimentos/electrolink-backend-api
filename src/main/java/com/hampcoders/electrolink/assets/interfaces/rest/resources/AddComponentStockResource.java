package com.hampcoders.electrolink.assets.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Resource used for adding a new stock item to a technician's inventory.
 */
public record AddComponentStockResource(
    @NotNull Long componentId,
    @Min(1) int quantity,
    @Min(0) int alertThreshold
) {}