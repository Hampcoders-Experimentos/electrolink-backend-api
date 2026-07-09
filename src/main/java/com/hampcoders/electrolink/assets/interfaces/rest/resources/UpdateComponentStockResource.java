package com.hampcoders.electrolink.assets.interfaces.rest.resources;

import jakarta.validation.constraints.Min;

/**
 * Resource used for updating the quantity and threshold of an existing component stock item.
 */
public record UpdateComponentStockResource(
    @Min(0) int newQuantity,
    @Min(0) int newAlertThreshold
) {}