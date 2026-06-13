package com.hampcoders.electrolink.assets.interfaces.rest.resources;

import java.util.List;
import java.util.UUID;

/**
 * Resource representing the full details of a technician's inventory, including its stock list.
 */
public record TechnicianInventoryResource(
    UUID inventoryId,
    Long technicianId,
    List<ComponentStockResource> stock
) {}