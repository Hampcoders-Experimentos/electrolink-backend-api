package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.commands.UpdateComponentCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.UpdateComponentResource;

/**
 * Assembler to convert UpdateComponentResource into an UpdateComponentCommand.
 */
public class UpdateComponentCommandFromResourceAssembler {
  /**
   * Converts the resource and component ID into an UpdateComponentCommand.
   *
   * @param componentId The ID of the component to update.
   * @param resource The UpdateComponentResource containing update details.
   * @return The resulting UpdateComponentCommand.
   */
  public static UpdateComponentCommand toCommandFromResource(
      final Long componentId, final UpdateComponentResource resource) {
    return new UpdateComponentCommand(
        componentId,
        resource.name(),
        resource.description(),
        resource.componentTypeId(),
        resource.isActive()
    );
  }
}