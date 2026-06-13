package com.hampcoders.electrolink.assets.interfaces.rest.controllers;

import com.hampcoders.electrolink.assets.domain.model.commands.DeletePropertyCommand;
import com.hampcoders.electrolink.assets.domain.model.queries.GetAllPropertiesByOwnerIdQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetAllPropertiesQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetPropertyByIdQuery;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.OwnerId;
import com.hampcoders.electrolink.assets.domain.services.PropertyCommandService;
import com.hampcoders.electrolink.assets.domain.services.PropertyQueryService;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreatePropertyResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.PropertyResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.UpdatePropertyResource;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.CreatePropertyCommandFromResourceAssembler;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.PropertyResourceFromEntityAssembler;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.UpdatePropertyCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
 * REST controller for managing property entities.
 */
@Tag(name = "Property Management", description = "Endpoints for managing properties")
@RestController
@RequestMapping(value = "/api/v1/properties", produces = MediaType.APPLICATION_JSON_VALUE)
public class PropertyController {
  private final PropertyCommandService propertyCommandService;
  private final PropertyQueryService propertyQueryService;

  /**
   * Constructs a PropertyController.
   *
   * @param propertyCommandService The command service for property modifications.
   * @param propertyQueryService The query service for property data retrieval.
   */
  public PropertyController(final PropertyCommandService propertyCommandService,
                            final PropertyQueryService propertyQueryService) {
    this.propertyCommandService = propertyCommandService;
    this.propertyQueryService = propertyQueryService;
  }

  /**
   * Fetches a list of all properties.
   *
   * @return A ResponseEntity containing a list of PropertyResources.
   */
  @Operation(summary = "Retrieve all properties",
      description = "Fetches a list of all properties.")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved the list of properties")
  })
  @GetMapping
  public ResponseEntity<List<PropertyResource>> getAllProperties() {
    var getAllPropertiesQuery = new GetAllPropertiesQuery();
    var properties = propertyQueryService.handle(getAllPropertiesQuery);
    var propertyResources = properties.stream()
        .map(PropertyResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return new ResponseEntity<>(propertyResources, HttpStatus.OK);
  }

  /**
   * Fetches a list of properties associated with a specific owner.
   *
   * @param ownerId The ID of the owner.
   * @return A ResponseEntity containing a list of PropertyResources.
   */
  @Operation(summary = "Retrieve properties by owner ID",
      description = "Fetches a list of properties associated with a specific owner.")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved the list of properties"),
      @ApiResponse(responseCode = "400",
          description = "Invalid owner ID format")
  })
  @GetMapping("/owner/{ownerId}")
  public ResponseEntity<List<PropertyResource>> getAllPropertiesByOwnerId(
      @Parameter(description = "UUID of the owner", required = true)
      @PathVariable final Long ownerId) {
    var getAllPropertiesByOwnerIdQuery = new GetAllPropertiesByOwnerIdQuery(new OwnerId(ownerId));
    var properties = propertyQueryService.handle(getAllPropertiesByOwnerIdQuery);

    var propertyResources = properties.stream()
        .map(PropertyResourceFromEntityAssembler::toResourceFromEntity)
        .toList();

    return new ResponseEntity<>(propertyResources, HttpStatus.OK);
  }

  /**
   * Fetches details of a specific property using its ID.
   *
   * @param propertyId The unique identifier (UUID) of the property.
   * @return A ResponseEntity containing the PropertyResource or 404.
   */
  @Operation(summary = "Retrieve a property by ID",
      description = "Fetches details of a specific property using its ID.")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved the property details"),
      @ApiResponse(responseCode = "404",
          description = "Property not found")
  })
  @GetMapping("/{propertyId}")
  public ResponseEntity<PropertyResource> getPropertyById(
      @Parameter(description = "UUID of the property", required = true)
      @PathVariable final UUID propertyId) {
    var getPropertyByIdQuery = new GetPropertyByIdQuery(propertyId);
    var property = propertyQueryService.handle(getPropertyByIdQuery);

    return property.map(p -> new ResponseEntity<>(
            PropertyResourceFromEntityAssembler.toResourceFromEntity(p),
            HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Creates a new property with the provided details.
   *
   * @param resource The details of the property to create.
   * @return A ResponseEntity containing the created PropertyResource or an error status.
   */
  @Operation(summary = "Create a new property",
      description = "Creates a new property with the provided details.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Successfully created the property"),
      @ApiResponse(responseCode = "400", description = "Invalid property details"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping
  public ResponseEntity<PropertyResource> createProperty(
      @Parameter(description = "Details of the property to create", required = true)
      @RequestBody @Valid final CreatePropertyResource resource) {
    var createPropertyCommand = CreatePropertyCommandFromResourceAssembler.toCommandFromResource(
        resource);
    var propertyId = propertyCommandService.handle(createPropertyCommand);

    if (propertyId == null) {
      return ResponseEntity.badRequest().build();
    }

    var getPropertyByIdQuery = new GetPropertyByIdQuery(propertyId);
    var property = propertyQueryService.handle(getPropertyByIdQuery);

    if (property.isEmpty()) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    var propertyResource = PropertyResourceFromEntityAssembler.toResourceFromEntity(
        property.get());
    return new ResponseEntity<>(propertyResource, HttpStatus.CREATED);
  }

  /**
   * Updates the details of an existing property.
   *
   * @param propertyId The unique identifier (UUID) of the property to update.
   * @param resource The updated details of the property.
   * @return A ResponseEntity containing the updated PropertyResource or 404.
   */
  @Operation(summary = "Update an existing property",
      description = "Updates the details of an existing property.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successfully updated the property"),
      @ApiResponse(responseCode = "404", description = "Property not found"),
      @ApiResponse(responseCode = "400", description = "Invalid property details")
  })
  @PutMapping("/{propertyId}")
  public ResponseEntity<PropertyResource> updateProperty(
      @Parameter(description = "UUID of the property to update", required = true)
      @PathVariable final UUID propertyId,
      @Parameter(description = "Updated details of the property", required = true)
      @RequestBody @Valid final UpdatePropertyResource resource) {

    var updatePropertyCommand = UpdatePropertyCommandFromResourceAssembler
        .toCommandFromResource(propertyId, resource);

    var updatedProperty = propertyCommandService.handle(updatePropertyCommand);

    return updatedProperty.map(property -> new ResponseEntity<>(
            PropertyResourceFromEntityAssembler.toResourceFromEntity(property),
            HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Deletes a property using its ID.
   *
   * @param propertyId The unique identifier (UUID) of the property to delete.
   * @return A ResponseEntity with status 204 or 404.
   */
  @Operation(summary = "Delete a property", description = "Deletes a property using its ID.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Successfully deleted the property"),
      @ApiResponse(responseCode = "404", description = "Property not found")
  })
  @DeleteMapping("/{propertyId}")
  public ResponseEntity<Void> deleteProperty(
      @Parameter(description = "UUID of the property to delete", required = true)
      @PathVariable final UUID propertyId) {
    var deletePropertyCommand = new DeletePropertyCommand(propertyId);
    Boolean result = propertyCommandService.handle(deletePropertyCommand);

    return result
        ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}