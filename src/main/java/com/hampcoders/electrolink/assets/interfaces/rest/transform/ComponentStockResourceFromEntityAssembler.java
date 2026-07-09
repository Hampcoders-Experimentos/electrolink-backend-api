package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.entities.ComponentStock;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentStockResource;

/**
 * Assembler to convert ComponentStock entities into ComponentStockResource.
 */
public class ComponentStockResourceFromEntityAssembler {

  /**
   * Converts the ComponentStock entity to a ComponentStockResource.
   *
   * @param entity The ComponentStock entity.
   * @return The resulting ComponentStockResource.
   */
  public static ComponentStockResource toResourceFromEntity(final ComponentStock entity) {
    return new ComponentStockResource(
        entity.getId(),
        entity.getComponent().getComponentUid(),
        entity.getComponent().getName(),
        entity.getQuantityAvailable(),
        entity.getAlertThreshold(),
        entity.getLastUpdated()
    );
  }

  private ComponentStockResourceFromEntityAssembler() {
  }
}