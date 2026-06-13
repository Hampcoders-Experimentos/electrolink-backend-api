package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.commands.AddComponentStockCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.AddComponentStockResource;

/**
 * Assembler to convert AddComponentStockResource into an AddComponentStockCommand.
 */
public class AddComponentStockCommandFromResourceAssembler {
  /**
   * Converts the resource and technician ID into an AddComponentStockCommand.
   *
   * @param technicianId The ID of the technician to whom the stock will be added.
   * @param resource The AddComponentStockResource containing new stock details.
   * @return The resulting AddComponentStockCommand.
   */
  public static AddComponentStockCommand toCommandFromResource(
      final Long technicianId, final AddComponentStockResource resource) {
    return new AddComponentStockCommand(
        technicianId,
        resource.componentId(),
        resource.quantity(),
        resource.alertThreshold()
    );
  }
}