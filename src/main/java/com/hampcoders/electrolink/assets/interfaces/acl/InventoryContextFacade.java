package com.hampcoders.electrolink.assets.interfaces.acl;

import com.hampcoders.electrolink.assets.domain.model.commands.CreateTechnicianInventoryCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.UpdateComponentStockCommand;
import com.hampcoders.electrolink.assets.domain.model.queries.GetInventoryByTechnicianIdQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetStockItemDetailsQuery;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.ComponentId;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.assets.domain.services.TechnicianInventoryCommandService;
import com.hampcoders.electrolink.assets.domain.services.TechnicianInventoryQueryService;
import com.hampcoders.electrolink.assets.interfaces.rest.resource.ComponentStockResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resource.TechnicianInventoryResource;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.TechnicianInventoryResourceFromEntityAssembler;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Anti-Corruption Layer (ACL) facade for exposing inventory management operations
 * to other bounded contexts (e.g., Profiles or shared contexts).
 */
@Service
public class InventoryContextFacade {

  private final TechnicianInventoryCommandService technicianInventoryCommandService;
  private final TechnicianInventoryQueryService technicianInventoryQueryService;

  /**
   * Constructs an InventoryContextFacade.
   *
   * @param technicianInventoryCommandService Command service for inventory modifications.
   * @param technicianInventoryQueryService Query service for inventory data retrieval.
   */
  public InventoryContextFacade(
      final TechnicianInventoryCommandService technicianInventoryCommandService,
      final TechnicianInventoryQueryService technicianInventoryQueryService) {
    this.technicianInventoryCommandService = technicianInventoryCommandService;
    this.technicianInventoryQueryService = technicianInventoryQueryService;
  }

  /**
   * Creates a new technician inventory.
   *
   * @param technicianId The ID of the technician.
   * @return The UUID of the newly created inventory.
   */
  public UUID createInventoryForTechnician(final Long technicianId) {
    var command = new CreateTechnicianInventoryCommand(new TechnicianId(technicianId));
    return technicianInventoryCommandService.handle(command);
  }

  /**
   * Fetches the technician inventory resource by technician ID.
   *
   * @param technicianId The ID of the technician.
   * @return An Optional containing the TechnicianInventoryResource, or empty if not found.
   */
  public Optional<TechnicianInventoryResource>
        fetchInventoryByTechnicianId(final Long technicianId) {
    var query = new GetInventoryByTechnicianIdQuery(new TechnicianId(technicianId));
    return technicianInventoryQueryService.handle(query)
        .map(TechnicianInventoryResourceFromEntityAssembler::toResourceFromEntity);
  }

  /**
   * Checks if an inventory exists for the given technician ID.
   *
   * @param technicianId The ID of the technician.
   * @return true if an inventory exists, false otherwise.
   */
  public boolean existsInventoryForTechnician(final Long technicianId) {
    var query = new GetInventoryByTechnicianIdQuery(new TechnicianId(technicianId));
    return technicianInventoryQueryService.handle(query).isPresent();
  }

  /**
   * Finds a specific component stock for a technician.
   *
   * @param technicianId The ID of the technician.
   * @param componentId  The ID of the component.
   * @return An Optional containing the ComponentStockResource, or empty if not found.
   */
  public Optional<ComponentStockResource> findComponentStock(final Long technicianId,
                                                              final Long componentId) {
    var query = new GetStockItemDetailsQuery(
        new TechnicianId(technicianId),
        new ComponentId(componentId));
    return technicianInventoryQueryService.handle(query)
        .map(TechnicianInventoryResourceFromEntityAssembler::toStockResourceFromEntity);
  }

  /**
   * Updates the quantity of a specific component stock for a technician.
   *
   * @param technicianId The ID of the technician.
   * @param componentId  The ID of the component.
   * @param newQuantity  The new quantity value.
   */
  public void updateComponentStock(final Long technicianId, final Long componentId,
                                   final int newQuantity) {
    var command = new UpdateComponentStockCommand(technicianId, componentId, newQuantity, null);
    technicianInventoryCommandService.handle(command);
  }
}