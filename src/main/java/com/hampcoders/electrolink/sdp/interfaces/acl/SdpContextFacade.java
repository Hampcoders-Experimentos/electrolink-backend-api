package com.hampcoders.electrolink.sdp.interfaces.acl;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestsByClientIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindServiceByIdQuery;
import com.hampcoders.electrolink.sdp.domain.services.RequestCommandService;
import com.hampcoders.electrolink.sdp.domain.services.RequestQueryService;
import com.hampcoders.electrolink.sdp.domain.services.ServiceQueryService;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.CreateRequestResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.CreateRequestCommandFromResourceAssembler;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.UpdateRequestCommandFromResourceAssembler;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Sdp Context Facade serves as an Application Context Facade
 * for the Service Delivery Platform (SDP) module.
 */
@Service
public class SdpContextFacade {

  private final RequestCommandService requestCommandService;
  private final RequestQueryService requestQueryService;
  private final ServiceQueryService serviceQueryService;

  /**
   * Constructor for Sdp ContextFacade, injecting necessary services.
   *
   * @param requestCommandService Service for handling request-related commands.
   * @param requestQueryService Service for handling request-related queries.
   * @param serviceQueryService Service for handling service-related queries.
   */
  public SdpContextFacade(RequestCommandService requestCommandService,
                          RequestQueryService requestQueryService,
                          ServiceQueryService serviceQueryService) {
    this.requestCommandService = requestCommandService;
    this.requestQueryService = requestQueryService;
    this.serviceQueryService = serviceQueryService;
  }

  /**
   * Fetches the service ID associated with a given request ID.
   *
   * @param requestId The ID of the request for which to fetch the service ID.
   * @return An Optional containing the service ID if found, or empty if not found.
   */
  public Optional<String> fetchRequestServiceId(Long requestId) {
    var query = new FindRequestByIdQuery(requestId);
    return requestQueryService.handle(query)
        .map(Request::getServiceId);
  }

  /**
   * Fetches the list of service component requirements for a given service ID.
   *
   * @param serviceId The ID of the service for which to fetch component requirements.
   * @return A list of ServiceComponentRequirement objects
   *     representing the components required for the service.
   */
  public List<ServiceComponentRequirement> fetchServiceComponentRequirements(Long serviceId) {
    var query = new FindServiceByIdQuery(serviceId);
    return serviceQueryService.handle(query)
        .map(service -> {
          if (service.getComponents() == null || service.getComponents().isEmpty()) {
            return Collections.<ServiceComponentRequirement>emptyList();
          }
          return service.getComponents().stream()
              .map(cq -> new ServiceComponentRequirement(
                  Long.parseLong(cq.getComponentId()),
                  cq.getQuantity()))
              .toList();
        })
        .orElse(Collections.emptyList());
  }

  /**
   * Fetches a list of request summaries for a given client ID.
   *
   * @param clientId The ID of the client for which to fetch request summaries.
   * @return A list of RequestSummary objects representing the requests for the client.
   */
  public List<RequestSummary> fetchRequestsByClientId(String clientId) {
    var query = new FindRequestsByClientIdQuery(clientId);
    return requestQueryService.handle(query).stream()
        .map(r -> new RequestSummary(r.getId(), r.getServiceId(), r.getScheduledDate()))
        .toList();
  }

  /**
   * Creates a new request based on the provided CreateRequestResource.
   *
   * @param resource The CreateRequestResource containing the details for the new request.
   * @return The ID of the newly created request.
   */
  public Long createRequest(CreateRequestResource resource) {
    var command = CreateRequestCommandFromResourceAssembler.toCommandFromResource(resource);
    return requestCommandService.handle(command).getId();
  }

  /**
   * Updates an existing request with the given request ID using the provided CreateRequestResource.
   *
   * @param requestId The ID of the request to be updated.
   * @param resource The CreateRequestResource containing the updated details for the request.
   * @return The ID of the updated request.
   */
  public Long updateRequest(Long requestId, CreateRequestResource resource) {
    var command = UpdateRequestCommandFromResourceAssembler
        .toCommandFromResource(requestId, resource);
    return requestCommandService.handle(command).getId();
  }

  /**
   * Deletes a request with the specified request ID.
   *
   * @param requestId The ID of the request to be deleted.
   */
  public void deleteRequest(Long requestId) {
    var command = new DeleteRequestCommand(requestId);
    requestCommandService.handle(command);
  }

  /**
   * Record representing the requirement of a service component,
   * including the component ID and quantity.
   *
   * @param componentId The ID of the service component required.
   * @param quantity The quantity of the service component required.
   */
  public record ServiceComponentRequirement(Long componentId, int quantity) {}

  /**
   * Record representing a summary of a request,
   * including the request ID, service ID, and scheduled date.
   *
   * @param requestId The ID of the request.
   * @param serviceId The ID of the service associated with the request.
   * @param scheduledDate The scheduled date for the request.
   */
  public record RequestSummary(Long requestId, String serviceId, LocalDate scheduledDate) {}
}
