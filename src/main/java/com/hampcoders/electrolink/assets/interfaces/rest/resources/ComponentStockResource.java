package com.hampcoders.electrolink.assets.interfaces.rest.resources;

import java.util.Date;
import java.util.UUID;

/**
 * Resource representing a component stock item within an inventory.
 */
public record ComponentStockResource(
    UUID componentStockId,
    Long componentId,
    String componentName,
    int quantityAvailable,
    int alertThreshold,
    Date lastUpdated
) {}