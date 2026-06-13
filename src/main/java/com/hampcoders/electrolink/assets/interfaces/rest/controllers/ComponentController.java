package com.hampcoders.electrolink.assets.interfaces.rest.controllers;

import com.hampcoders.electrolink.assets.domain.model.commands.DeleteComponentCommand;
import com.hampcoders.electrolink.assets.domain.model.queries.GetAllComponentsQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetComponentByIdQuery;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.ComponentId;
import com.hampcoders.electrolink.assets.domain.services.ComponentCommandService;
import com.hampcoders.electrolink.assets.domain.services.ComponentQueryService;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreateComponentResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.UpdateComponentResource;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.ComponentResourceFromEntityAssembler;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.CreateComponentCommandFromResourceAssembler;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.UpdateComponentCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * REST controller for managing component lifecycle and retrieval operations.
 */
@Tag(name = "Component Management", description = "API for managing components")
@RestController
@RequestMapping(value = "/api/v1/components", produces = MediaType.APPLICATION_JSON_VALUE)
public class ComponentController {

  private final ComponentCommandService componentCommandService;
  private final ComponentQueryService componentQueryService;

  /**
   * Constructs a ComponentController.
   *
   * @param componentCommandService The command service for component modifications.
   * @param componentQueryService The query service for component data retrieval.
   */
  public ComponentController(final ComponentCommandService componentCommandService,
                             final ComponentQueryService componentQueryService) {
    this.componentCommandService = componentCommandService;
    this.componentQueryService = componentQueryService;
  }

  /**
   * Retrieves a specific component using its unique identifier.
   *
   * @param componentId The unique identifier of the component.
   * @return A ResponseEntity containing the ComponentResource or 404.
   */
  @Operation(summary = "Get a component by ID", description = "Retrieve a "
      + "specific component using its unique identifier.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Component found"),
      @ApiResponse(responseCode = "404", description = "Component not found")
  })
  @GetMapping("/{componentId}")
  public ResponseEntity<ComponentResource> getComponentById(
      @Parameter(description = "Long of the component to retrieve")
      @PathVariable final Long componentId) {
    var getComponentByIdQuery = new GetComponentByIdQuery(new ComponentId(componentId));
    var component = componentQueryService.handle(getComponentByIdQuery);

    return component.map(p -> new ResponseEntity<>(
            ComponentResourceFromEntityAssembler.toResourceFromEntity(p), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Retrieves a list of all components.
   *
   * @return A ResponseEntity containing a list of ComponentResources.
   */
  @Operation(summary = "Get all components", description = "Retrieve a list of all components.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "List of components retrieved successfully")
  })
  @GetMapping
  public ResponseEntity<List<ComponentResource>> getAllComponents() {
    var getAllComponentsQuery = new GetAllComponentsQuery();
    var components = componentQueryService.handle(getAllComponentsQuery);
    var componentResources = components.stream()
        .map(ComponentResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return new ResponseEntity<>(componentResources, HttpStatus.OK);
  }

  /**
   * Creates a new component with the provided details.
   *
   * @param resource The details of the component to create.
   * @return A ResponseEntity containing the created ComponentResource or an error status.
   */
  @Operation(summary = "Create a new component",
      description = "Create a new component with the provided details.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Component created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping
  public ResponseEntity<ComponentResource> createComponent(
      @Parameter(description = "Details of the component to create")
      @RequestBody @Valid final CreateComponentResource resource) {
    var createComponentCommand = CreateComponentCommandFromResourceAssembler.toCommandFromResource(
        resource);
    var componentId = componentCommandService.handle(createComponentCommand);

    if (componentId == null || componentId.componentId() == null) {
      return ResponseEntity.badRequest().build();
    }

    var getComponentByIdQuery = new GetComponentByIdQuery(componentId);
    var component = componentQueryService.handle(getComponentByIdQuery);

    if (component.isEmpty()) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    var componentResource = ComponentResourceFromEntityAssembler.toResourceFromEntity(
        component.get());
    return new ResponseEntity<>(componentResource, HttpStatus.CREATED);
  }

  /**
   * Updates the details of an existing component.
   *
   * @param componentId The unique identifier of the component to update.
   * @param resource The updated details of the component.
   * @return A ResponseEntity containing the updated ComponentResource or 404.
   */
  @Operation(summary = "Update an existing component",
      description = "Update the details of an existing component.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Component updated successfully"),
      @ApiResponse(responseCode = "404", description = "Component not found")
  })
  @PutMapping("/{componentId}")
  public ResponseEntity<ComponentResource> updateComponent(
      @Parameter(description = "Long of the component to update")
      @PathVariable final Long componentId,
      @Parameter(description = "Updated details of the component")
      @RequestBody @Valid final UpdateComponentResource resource) {
    var updateComponentCommand = UpdateComponentCommandFromResourceAssembler.toCommandFromResource(
        componentId, resource);
    var updatedComponent = componentCommandService.handle(updateComponentCommand);

    return updatedComponent.map(component -> new ResponseEntity<>(
            ComponentResourceFromEntityAssembler.toResourceFromEntity(component), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Deletes a specific component using its unique identifier.
   *
   * @param componentId The unique identifier of the component to delete.
   * @return A ResponseEntity with status 204 or 404.
   */
  @Operation(summary = "Delete a component",
      description = "Delete a specific component using its unique identifier.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Component deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Component not found")
  })
  @DeleteMapping("/{componentId}")
  public ResponseEntity<Void> deleteComponent(
      @Parameter(description = "Long of the component to delete")
      @PathVariable final Long componentId) {
    var deleteComponentCommand = new DeleteComponentCommand(componentId);
    Boolean result = componentCommandService.handle(deleteComponentCommand);

    return result
        ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}