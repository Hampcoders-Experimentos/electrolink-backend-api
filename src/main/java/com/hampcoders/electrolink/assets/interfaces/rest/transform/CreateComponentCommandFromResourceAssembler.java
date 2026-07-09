package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.commands.CreateComponentCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreateComponentResource;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Assembler to convert CreateComponentResource into a CreateComponentCommand.
 */
@Component
public class CreateComponentCommandFromResourceAssembler {
  /**
   * Converts the resource into a CreateComponentCommand.
   *
   * @param resource The CreateComponentResource containing component creation details.
   * @return The resulting CreateComponentCommand.
   */
  public static CreateComponentCommand toCommandFromResource(
      final CreateComponentResource resource) {
    UUID componentId = UUID.randomUUID();
    return new CreateComponentCommand(
        componentId,
        resource.name(),
        resource.description(),
        resource.componentTypeId(),
        resource.isActive() != null ? resource.isActive() : true
    );
  }
}