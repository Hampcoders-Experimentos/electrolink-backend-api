package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.commands.CreateComponentTypeCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreateComponentTypeResource;
import org.springframework.stereotype.Component;

/**
 * Assembler to convert CreateComponentTypeResource into a CreateComponentTypeCommand.
 */
@Component
public class CreateComponentTypeCommandFromResourceAssembler {
  /**
   * Converts the resource into a CreateComponentTypeCommand.
   *
   * @param resource The CreateComponentTypeResource containing type creation details.
   * @return The resulting CreateComponentTypeCommand.
   */
  public static CreateComponentTypeCommand toCommandFromResource(
      final CreateComponentTypeResource resource) {
    return new CreateComponentTypeCommand(resource.name(), resource.description());
  }
}