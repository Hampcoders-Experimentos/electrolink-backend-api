package com.hampcoders.electrolink.sdp.interfaces.rest.controllers;

import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindServiceByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.GetAllServicesQuery;
import com.hampcoders.electrolink.sdp.domain.services.ServiceCommandService;
import com.hampcoders.electrolink.sdp.domain.services.ServiceQueryService;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateServiceResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.ServiceResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.CreateServiceCommandFromResourceAssembler;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.ServiceResourceFromEntityAssembler;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.UpdateServiceCommandFromResourceAssembler;
import java.util.List;
import org.springframework.http.HttpStatus;
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
 * REST controller for managing services in the Service Delivery Platform (SDP).
 */
@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

  private final ServiceCommandService commandService;
  private final ServiceQueryService queryService;

  /**
   * Constructor for ServiceController.
   *
   * @param commandService the service command service for handling commands related to services
   * @param queryService the service query service for handling queries related to services
   */
  public ServiceController(ServiceCommandService commandService,
                           ServiceQueryService queryService) {
    this.commandService = commandService;
    this.queryService = queryService;
  }

  /**
   * Endpoint to retrieve all services.
   *
   * @return a ResponseEntity containing a list of ServiceResource objects representing all services
   */
  @GetMapping
  public ResponseEntity<List<ServiceResource>> getAllServices() {
    var services = queryService.handle(new GetAllServicesQuery());
    var resources = services.stream()
        .map(ServiceResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return ResponseEntity.ok(resources);
  }

  /**
   * Endpoint to create a new service.
   *
   * @param resource the CreateServiceResource object containing the details
   *     of the service to be created
   */
  @PostMapping
  public ResponseEntity<ServiceResource> create(@RequestBody CreateServiceResource resource) {
    var command = CreateServiceCommandFromResourceAssembler.toCommandFromResource(resource);
    var serviceId = commandService.handle(command);
    var query = new FindServiceByIdQuery(serviceId);
    var service = queryService.handle(query);
    return service.map(s -> new ResponseEntity<>(
            ServiceResourceFromEntityAssembler.toResourceFromEntity(s), HttpStatus.CREATED))
        .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
  }

  /**
   * Endpoint to update an existing service.
   *
   * @param serviceId the ID of the service to be updated
   * @param resource the CreateServiceResource object containing the updated details of the service
   * @return a ResponseEntity indicating the result of the update operation
   */
  @PutMapping("/{serviceId}")
  public ResponseEntity<Void> update(@PathVariable Long serviceId,
                                     @RequestBody CreateServiceResource resource) {
    var command = UpdateServiceCommandFromResourceAssembler
        .toCommandFromResource(serviceId, resource);
    commandService.handle(command);
    return ResponseEntity.ok().build();
  }

  /**
   * Endpoint to delete an existing service.
   *
   * @param serviceId the ID of the service to be deleted
   * @return a ResponseEntity indicating the result of the delete operation
   */
  @DeleteMapping("/{serviceId}")
  public ResponseEntity<Void> delete(@PathVariable Long serviceId) {
    var command = new DeleteServiceCommand(serviceId);
    commandService.handle(command);
    return ResponseEntity.ok().build();
  }

  /**
   * Endpoint to retrieve a service by its ID.
   *
   * @param serviceId the ID of the service to be retrieved
   * @return a ResponseEntity containing a ServiceResource object
   *     representing the service if found,
   *     or a 404 Not Found response if the service does not exist
   */
  @GetMapping("/{serviceId}")
  public ResponseEntity<ServiceResource> getById(@PathVariable Long serviceId) {
    var query = new FindServiceByIdQuery(serviceId);
    return queryService.handle(query)
        .map(s -> ResponseEntity.ok(ServiceResourceFromEntityAssembler.toResourceFromEntity(s)))
        .orElse(ResponseEntity.notFound().build());
  }
}
