package com.hampcoders.electrolink.sdp.interfaces.rest.controllers;

import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestsByClientIdQuery;
import com.hampcoders.electrolink.sdp.domain.services.RequestCommandService;
import com.hampcoders.electrolink.sdp.domain.services.RequestQueryService;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateRequestResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.RequestResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.CreateRequestCommandFromResourceAssembler;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.RequestResourceFromEntityAssembler;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.UpdateRequestCommandFromResourceAssembler;
import com.hampcoders.electrolink.shared.interfaces.rest.resources.MessageResource;
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
 * REST controller for managing requests in the SDP application.
 */
@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

  private final RequestCommandService requestCommandService;
  private final RequestQueryService requestQueryService;

  /**
   * Constructor for RequestController.
   *
   * @param requestCommandService the service for handling request commands
   * @param requestQueryService the service for handling request queries
   */
  public RequestController(RequestCommandService requestCommandService,
                           RequestQueryService requestQueryService) {
    this.requestCommandService = requestCommandService;
    this.requestQueryService = requestQueryService;
  }

  /**
   * Endpoint to create a new request.
   *
   * @param resource the resource containing request details
   * @return a response entity with a message indicating the result of the operation
   */
  @PostMapping
  public ResponseEntity<MessageResource> createRequest(
      @RequestBody CreateRequestResource resource) {
    var command = CreateRequestCommandFromResourceAssembler.toCommandFromResource(resource);
    var savedRequest = requestCommandService.handle(command);
    return new ResponseEntity<>(new MessageResource("Request created with ID: "
        + savedRequest.getId()), HttpStatus.CREATED);
  }

  /**
   * Endpoint to retrieve a request by its ID.
   *
   * @param id the ID of the request to retrieve
   * @return a response entity with the requested request or a not found error
   */
  @GetMapping("/{id}")
  public ResponseEntity<RequestResource> getRequestById(@PathVariable Long id) {
    var query = new FindRequestByIdQuery(id);
    return requestQueryService.handle(query)
        .map(r -> ResponseEntity.ok(RequestResourceFromEntityAssembler.toResourceFromEntity(r)))
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Endpoint to retrieve all requests associated with a specific client ID.
   *
   * @param clientId the ID of the client whose requests to retrieve
   * @return a response entity with a list of requests associated with the client
   */
  @GetMapping("/clients/{clientId}/requests")
  public ResponseEntity<List<RequestResource>> getRequestsByClient(@PathVariable String clientId) {
    var query = new FindRequestsByClientIdQuery(clientId);
    var requests = requestQueryService.handle(query);
    var resources = requests.stream()
        .map(RequestResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return ResponseEntity.ok(resources);
  }

  /**
   * Endpoint to update an existing request.
   *
   * @param id the ID of the request to update
   * @param resource the resource containing updated request details
   * @return a response entity with a message indicating the result of the operation
   */
  @PutMapping("/{id}")
  public ResponseEntity<MessageResource> updateRequest(
      @PathVariable Long id,
      @RequestBody CreateRequestResource resource) {
    var command = UpdateRequestCommandFromResourceAssembler.toCommandFromResource(id, resource);
    requestCommandService.handle(command);
    return ResponseEntity.ok(new MessageResource("Request updated successfully."));
  }

  /**
   * Endpoint to delete a request by its ID.
   *
   * @param id the ID of the request to delete
   * @return a response entity with a message indicating the result of the operation
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResource> deleteRequest(@PathVariable Long id) {
    var command = new DeleteRequestCommand(id);
    requestCommandService.handle(command);
    return ResponseEntity.ok(new MessageResource("Request deleted successfully."));
  }
}
