package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.commands.UpdateComponentStockCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.UpdateComponentStockResource;

/**
 * Assembler to convert UpdateComponentStockResource into an UpdateComponentStockCommand.
 */
public class UpdateComponentStockCommandFromResourceAssembler {
  /**
   * Converts the resource, technician ID, and component ID into an UpdateComponentStockCommand.
   *
   * @param technicianId The ID of the technician whose stock is being updated.
   * @param componentId The ID of the component stock item to update.
   * @param resource The UpdateComponentStockResource containing new quantity and threshold.
   * @return The resulting UpdateComponentStockCommand.
   */
  public static UpdateComponentStockCommand toCommandFromResource(
      final Long technicianId, final Long componentId,
      final UpdateComponentStockResource resource) {
    return new UpdateComponentStockCommand(
        technicianId,
        componentId,
        resource.newQuantity(),
        resource.newAlertThreshold()
    );
  }
}