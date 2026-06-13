package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.aggregates.ComponentType;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentTypeResource;
import org.springframework.stereotype.Component;

/**
 * Assembler to convert ComponentType entities into ComponentTypeResource.
 */
@Component
public class ComponentTypeResourceFromEntityAssembler {
  /**
   * Converts the ComponentType entity to a ComponentTypeResource.
   *
   * @param entity The ComponentType entity.
   * @return The resulting ComponentTypeResource.
   */
  public static ComponentTypeResource toResourceFromEntity(final ComponentType entity) {
    return new ComponentTypeResource(
        entity.getId(),
        entity.getName(),
        entity.getDescription()
    );
  }
}