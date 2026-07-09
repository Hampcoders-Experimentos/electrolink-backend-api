package com.hampcoders.electrolink.assets.interfaces.rest.controllers;

import com.hampcoders.electrolink.assets.domain.model.queries.GetAllComponentTypesQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetComponentTypeByIdQuery;
import com.hampcoders.electrolink.assets.domain.services.ComponentTypeCommandService;
import com.hampcoders.electrolink.assets.domain.services.ComponentTypeQueryService;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentTypeResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreateComponentTypeResource;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.ComponentTypeResourceFromEntityAssembler;
import com.hampcoders.electrolink.assets.interfaces.rest.transform.CreateComponentTypeCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing component type entities.
 */
@Tag(name = "Component Types", description = "API for managing component types")
@RestController
@RequestMapping(value = "/api/v1/component-types", produces = MediaType.APPLICATION_JSON_VALUE)
public class ComponentTypeController {

  private final ComponentTypeCommandService componentTypeCommandService;
  private final ComponentTypeQueryService componentTypeQueryService;

  /**
   * Constructs a ComponentTypeController.
   *
   * @param componentTypeCommandService The command service for component type modifications.
   * @param componentTypeQueryService The query service for component type data retrieval.
   */
  public ComponentTypeController(final ComponentTypeCommandService componentTypeCommandService,
                                 final ComponentTypeQueryService componentTypeQueryService) {
    this.componentTypeCommandService = componentTypeCommandService;
    this.componentTypeQueryService = componentTypeQueryService;
  }

  /**
   * Retrieves a list of all available component types.
   *
   * @return A ResponseEntity containing a list of ComponentTypeResources.
   */
  @Operation(summary = "Get all component types",
      description = "Retrieve a list of all available component types.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Successfully retrieved the list of component types",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ComponentTypeResource.class)))
  })
  @GetMapping
  public ResponseEntity<List<ComponentTypeResource>> getAllComponentTypes() {
    var getAllComponentTypesQuery = new GetAllComponentTypesQuery();
    var componentTypes = componentTypeQueryService.handle(getAllComponentTypesQuery);

    var componentTypeResources = componentTypes.stream()
        .map(ComponentTypeResourceFromEntityAssembler::toResourceFromEntity)
        .toList();

    return new ResponseEntity<>(componentTypeResources, HttpStatus.OK);
  }

  /**
   * Creates a new component type and returns its details.
   *
   * @param resource The details of the component type to create.
   * @return A ResponseEntity containing the created ComponentTypeResource or an error status.
   */
  @Operation(summary = "Create a new component type",
      description = "Create a new component type and return its details.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully created the component type",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ComponentTypeResource.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping
  public ResponseEntity<ComponentTypeResource> createComponentType(
      @RequestBody @Valid final CreateComponentTypeResource resource) {
    var createComponentTypeCommand =
        CreateComponentTypeCommandFromResourceAssembler.toCommandFromResource(
        resource);

    var componentTypeId = componentTypeCommandService.handle(createComponentTypeCommand);

    if (componentTypeId == null) {
      return ResponseEntity.badRequest().build();
    }

    var getComponentTypeByIdQuery = new GetComponentTypeByIdQuery(componentTypeId);
    var componentType = componentTypeQueryService.handle(getComponentTypeByIdQuery);

    if (componentType.isEmpty()) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    var componentTypeResource = ComponentTypeResourceFromEntityAssembler.toResourceFromEntity(
        componentType.get());

    return new ResponseEntity<>(componentTypeResource, HttpStatus.CREATED);
  }
}