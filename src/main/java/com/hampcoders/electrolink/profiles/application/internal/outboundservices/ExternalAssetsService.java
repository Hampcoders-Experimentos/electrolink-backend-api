package com.hampcoders.electrolink.profiles.application.internal.outboundservices;

import com.hampcoders.electrolink.assets.interfaces.acl.InventoryContextFacade;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling external asset-related operations,
 * such as creating an inventory for a technician.
 * This service interacts with the InventoryContextFacade
 * to perform necessary actions related to inventory management for technicians
 */
@Service
public class ExternalAssetsService {
  private final InventoryContextFacade inventoryContextFacade;

  /**
   * External Assets Service Constructor.
   *
   * @param inventoryContextFacade The InventoryContextFacade used to interact
   *     with the inventory management system.
   */
  public ExternalAssetsService(InventoryContextFacade inventoryContextFacade) {
    this.inventoryContextFacade = inventoryContextFacade;
  }

  /**
   * Creates an inventory for a technician if it does not already exist.
   *
   * @param technicianProfileId The ID of the technician profile for which to create the inventory.
   */
  public void createInventoryForTechnician(Long technicianProfileId) {
    if (!inventoryContextFacade.existsInventoryForTechnician(technicianProfileId)) {
      inventoryContextFacade.createInventoryForTechnician(technicianProfileId);
    }
  }
}