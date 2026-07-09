package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Component;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentResource;


/**
 * Assembler to convert Component entities into ComponentResource.
 */
@org.springframework.stereotype.Component

public class ComponentResourceFromEntityAssembler {
  /**
   * Converts the Component entity to a ComponentResource.
   *
   * @param entity The Component entity.
   * @return The resulting ComponentResource.
   */
  public static ComponentResource toResourceFromEntity(final Component entity) {
    return new ComponentResource(
        entity.getComponentUid().toString(),
        entity.getName(),
        entity.getDescription(),
        entity.getIsActive(),
        entity.getComponentTypeId()
    );
  }
}