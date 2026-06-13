package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Component;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentLookupResource;

/**
 * Assembler to convert Component entities into ComponentLookupResource.
 */
public final class ComponentLookupResourceFromEntityAssembler {
  /**
   * Converts the Component entity to a ComponentLookupResource.
   *
   * @param entity The Component entity.
   * @return The resulting ComponentLookupResource.
   */
  public static ComponentLookupResource toResource(final Component entity) {
    return new ComponentLookupResource(entity.getComponentUid(), entity.getName());
  }
}