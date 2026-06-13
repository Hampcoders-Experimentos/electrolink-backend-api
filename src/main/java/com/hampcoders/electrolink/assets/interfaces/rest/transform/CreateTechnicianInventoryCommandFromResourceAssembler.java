package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.commands.CreateTechnicianInventoryCommand;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreateTechnicianInventoryResource;

/**
 * Assembler to convert CreateTechnicianInventoryResource into a CreateTechnicianInventoryCommand.
 */
public class CreateTechnicianInventoryCommandFromResourceAssembler {
  /**
   * Converts the resource into a CreateTechnicianInventoryCommand.
   *
   * @param resource The CreateTechnicianInventoryResource containing the technician ID.
   * @return The resulting CreateTechnicianInventoryCommand.
   */
  public static CreateTechnicianInventoryCommand toCommandFromResource(
      final CreateTechnicianInventoryResource resource) {
    return new CreateTechnicianInventoryCommand(new TechnicianId(resource.technicianId()));
  }
}