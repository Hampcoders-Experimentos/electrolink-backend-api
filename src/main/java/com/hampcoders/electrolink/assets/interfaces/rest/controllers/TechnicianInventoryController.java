package com.hampcoders.electrolink.assets.interfaces.rest.controllers;

import com.hampcoders.electrolink.assets.application.internal.outboundservices.ExternalProfileService;
import com.hampcoders.electrolink.assets.domain.model.commands.CreateTechnicianInventoryCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.DeleteComponentStockCommand;
import com.hampcoders.electrolink.assets.domain.model.queries.GetInventoriesWithLowStockQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetInventoryByTechnicianIdQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetStockItemDetailsQuery;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.ComponentId;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.assets.domain.services.TechnicianInventoryCommandService;
import com.hampcoders.electrolink.assets.domain.services.TechnicianInventoryQueryService;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.AddComponentStockResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentStockResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.TechnicianInventoryResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.UpdateComponentStockResource;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.AddComponentStockCommandFromResourceAssembler;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.ComponentStockResourceFromEntityAssembler;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.TechnicianInventoryResourceFromEntityAssembler;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.UpdateComponentStockCommandFromResourceAssembler;
import com.hampcoders.electrolink.iam.infrastructure.authorization.sfs.services.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing technician inventories and their component stock.
 */
@Tag(name = "Technician Inventories",
    description = "API for managing stock and components in technician inventories.")
@RestController
@RequestMapping(value = "/api/v1/technician-inventories",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class TechnicianInventoryController {

  private final TechnicianInventoryCommandService inventoryCommandService;
  private final TechnicianInventoryQueryService inventoryQueryService;
  private final AuthenticatedUserService authenticatedUserService;
  private final ExternalProfileService externalProfileService;

  /**
   * Constructs a TechnicianInventoryController.
   *
   * @param inventoryCommandService The command service for inventory modifications.
   * @param inventoryQueryService The query service for inventory data retrieval.
   * @param authenticatedUserService Service to get current authenticated user.
   * @param externalProfileService Service to fetch external technician data.
   */
  public TechnicianInventoryController(
      final TechnicianInventoryCommandService inventoryCommandService,
      final TechnicianInventoryQueryService inventoryQueryService,
      final AuthenticatedUserService authenticatedUserService,
      final ExternalProfileService externalProfileService
  ) {
    this.inventoryCommandService = inventoryCommandService;
    this.inventoryQueryService = inventoryQueryService;
    this.authenticatedUserService = authenticatedUserService;
    this.externalProfileService = externalProfileService;
  }

  /**
   * Retrieves all inventories that have components below a certain stock threshold.
   *
   * @return A list of TechnicianInventoryResource with low stock.
   */
  @Operation(summary = "Get inventories with low stock",
      description = "Retrieves all inventories where stock "
          + "for any component is below the defined threshold (default 5).")
  @GetMapping("/low-stock")
  public ResponseEntity<List<TechnicianInventoryResource>> getInventoriesWithLowStock() {
    var query = new GetInventoriesWithLowStockQuery(5);
    var inventories = inventoryQueryService.handle(query);

    var resources = inventories.stream()
        .map(TechnicianInventoryResourceFromEntityAssembler::toResourceFromEntity)
        .toList();

    return new ResponseEntity<>(resources, HttpStatus.OK);
  }

  /**
   * Retrieves the details of a specific component stock item within a technician's inventory.
   *
   * @param technicianId The ID of the technician owner.
   * @param componentId The ID of the component stock item.
   * @return The ComponentStockResource details or 404.
   */
  @Operation(summary = "Get stock item details",
      description = "Retrieves a specific component stock item from a technician's inventory.")
  @GetMapping("/technician/{technicianId}/stocks/{componentId}")
  public ResponseEntity<ComponentStockResource> getStockItemDetails(
      @Parameter(description = "ID of the technician") @PathVariable final Long technicianId,
      @Parameter(description = "ID of the component") @PathVariable final Long componentId) {

    var query = new GetStockItemDetailsQuery(
        new TechnicianId(technicianId),
        new ComponentId(componentId)
    );
    var stockItem = inventoryQueryService.handle(query);

    return stockItem.map(item -> new ResponseEntity<>(
            ComponentStockResourceFromEntityAssembler.toResourceFromEntity(item),
            HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Retrieves the entire inventory of a specific technician.
   *
   * @param technicianId The ID of the technician owner.
   * @return The TechnicianInventoryResource or 404.
   */
  @Operation(summary = "Get inventory by technician ID",
      description = "Retrieves the full inventory for a specific technician.")
  @GetMapping("/technician/{technicianId}")
  public ResponseEntity<TechnicianInventoryResource> getInventoryByTechnicianId(
      @Parameter(description = "ID of the technician") @PathVariable final Long technicianId) {
    var query = new GetInventoryByTechnicianIdQuery(new TechnicianId(technicianId));
    var inventory = inventoryQueryService.handle(query);

    return inventory.map(inv -> new ResponseEntity<>(
            TechnicianInventoryResourceFromEntityAssembler.toResourceFromEntity(inv),
            HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Creates a new inventory for the currently authenticated technician.
   *
   * @return The created TechnicianInventoryResource or an error status.
   */
  @Operation(summary = "Create technician inventory (Authenticated)",
      description = "Creates a new inventory linked to the currently "
          + "authenticated user's technician profile.")
  @PostMapping
  public ResponseEntity<TechnicianInventoryResource> createTechnicianInventory() {
    var emailOptional = authenticatedUserService.getAuthenticatedEmail();

    if (emailOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    var technicianIdOpt = externalProfileService.fetchTechnicianIdByEmail(emailOptional.get());

    if (technicianIdOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    var command = new CreateTechnicianInventoryCommand(technicianIdOpt.get());
    var inventoryId = inventoryCommandService.handle(command);

    if (inventoryId == null) {
      return ResponseEntity.badRequest().build();
    }

    var query = new GetInventoryByTechnicianIdQuery(technicianIdOpt.get());
    var inventory = inventoryQueryService.handle(query);

    return inventory
        .map(inv -> new ResponseEntity<>(
            TechnicianInventoryResourceFromEntityAssembler.toResourceFromEntity(inv),
            HttpStatus.CREATED))
        .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
  }

  /**
   * Adds new stock or updates quantity for an existing component in the inventory.
   *
   * @param technicianId The ID of the technician owner.
   * @param resource The details of the component stock to add/update.
   * @return The updated TechnicianInventoryResource or 404.
   */
  @Operation(summary = "Add component to stock",
      description = "Adds a component to the technician's stock "
          + "or increases the quantity if it exists.")
  @PostMapping("/technician/{technicianId}/stocks")
  public ResponseEntity<TechnicianInventoryResource> addComponentToStock(
      @Parameter(description = "ID of the technician") @PathVariable final Long technicianId,
      @RequestBody @Valid final AddComponentStockResource resource) {

    var command = AddComponentStockCommandFromResourceAssembler.toCommandFromResource(technicianId,
        resource);
    var updatedInventory = inventoryCommandService.handle(command);

    return updatedInventory.map(inv -> new ResponseEntity<>(
            TechnicianInventoryResourceFromEntityAssembler.toResourceFromEntity(inv),
            HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Updates the quantity and/or alert threshold of an existing component stock item.
   *
   * @param technicianId The ID of the technician owner.
   * @param componentId The ID of the component stock item.
   * @param resource The new quantity and threshold values.
   * @return The updated TechnicianInventoryResource or 404.
   */
  @Operation(summary = "Update component stock",
      description = "Updates the quantity and alert threshold "
          + "of an existing component in the inventory.")
  @PutMapping("/technician/{technicianId}/stocks/{componentId}")
  public ResponseEntity<TechnicianInventoryResource> updateComponentStock(
      @Parameter(description = "ID of the technician") @PathVariable final Long technicianId,
      @Parameter(description = "ID of the component") @PathVariable final Long componentId,
      @RequestBody @Valid final UpdateComponentStockResource resource) {

    var command = UpdateComponentStockCommandFromResourceAssembler.toCommandFromResource(
        technicianId, componentId, resource);
    var updatedInventory = inventoryCommandService.handle(command);

    return updatedInventory.map(inv -> new ResponseEntity<>(
            TechnicianInventoryResourceFromEntityAssembler.toResourceFromEntity(inv),
            HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Deletes a component stock item from the technician's inventory.
   *
   * @param technicianId The ID of the technician owner.
   * @param componentId The ID of the component stock item to delete.
   * @return A ResponseEntity with status 204 or 404.
   */
  @Operation(summary = "Delete component from inventory",
      description = "Deletes a specific component stock item from the technician's inventory.")
  @DeleteMapping("/technician/{technicianId}/stocks/{componentId}")
  public ResponseEntity<Void> deleteComponentFromInventory(
      @Parameter(description = "ID of the technician") @PathVariable final Long technicianId,
      @Parameter(description = "ID of the component") @PathVariable final Long componentId) {

    var command = new DeleteComponentStockCommand(technicianId, componentId);
    boolean result = inventoryCommandService.handle(command);

    return result
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}