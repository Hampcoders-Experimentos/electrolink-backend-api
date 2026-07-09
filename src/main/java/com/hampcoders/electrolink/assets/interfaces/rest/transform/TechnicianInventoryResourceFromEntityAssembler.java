package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.aggregates.TechnicianInventory;
import com.hampcoders.electrolink.assets.domain.model.entities.ComponentStock;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentStockResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.TechnicianInventoryResource;
import java.util.stream.Collectors;

/**
 * Assembler to convert TechnicianInventory entities into TechnicianInventoryResource.
 */
public class TechnicianInventoryResourceFromEntityAssembler {

  /**
   * Converts the TechnicianInventory entity to a TechnicianInventoryResource.
   *
   * @param entity The TechnicianInventory entity.
   * @return The resulting TechnicianInventoryResource.
   */
  public static TechnicianInventoryResource toResourceFromEntity(final TechnicianInventory entity) {
    var stockResources = entity.getComponentStocks().stream()
        .map(TechnicianInventoryResourceFromEntityAssembler::toStockResourceFromEntity)
        .collect(Collectors.toList());

    return new TechnicianInventoryResource(
        entity.getId(),
        entity.getTechnicianId(),
        stockResources
    );
  }

  /**
   * Converts a ComponentStock entity to a ComponentStockResource.
   *
   * @param stockEntity The ComponentStock entity.
   * @return The resulting ComponentStockResource.
   */
  public static ComponentStockResource toStockResourceFromEntity(
      final ComponentStock stockEntity) {
    return new ComponentStockResource(
        stockEntity.getId(),
        stockEntity.getComponent().getComponentUid(),
        stockEntity.getComponent().getName(),
        stockEntity.getQuantityAvailable(),
        stockEntity.getAlertThreshold(),
        stockEntity.getLastUpdated()
    );
  }
}