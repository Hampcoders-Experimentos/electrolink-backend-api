package com.hampcoders.electrolink.sdp.interfaces.acl;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestsByClientIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindServiceByIdQuery;
import com.hampcoders.electrolink.sdp.domain.services.RequestCommandService;
import com.hampcoders.electrolink.sdp.domain.services.RequestQueryService;
import com.hampcoders.electrolink.sdp.domain.services.ServiceQueryService;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateRequestResource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Facade for the Service Delivery Platform (SDP) context.
 * This class provides a simplified interface for interacting with the request services.
 * It acts as an Anti-Corruption Layer (ACL) to protect the domain from external changes.
 * All methods return primitive types or simple DTOs, not domain models.
 */
@Service
public class SdpContextFacade {

  private final RequestCommandService requestCommandService;
  private final RequestQueryService requestQueryService;
  private final ServiceQueryService serviceQueryService;

  public SdpContextFacade(RequestCommandService requestCommandService,
                          RequestQueryService requestQueryService,
                          ServiceQueryService serviceQueryService) {
    this.requestCommandService = requestCommandService;
    this.requestQueryService = requestQueryService;
    this.serviceQueryService = serviceQueryService;
  }

  public Optional<String> fetchRequestServiceId(Long requestId) {
    var query = new FindRequestByIdQuery(requestId);
    return requestQueryService.handle(query)
        .map(Request::getServiceId);
  }

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

  public List<RequestSummary> fetchRequestsByClientId(String clientId) {
    var query = new FindRequestsByClientIdQuery(clientId);
    return requestQueryService.handle(query).stream()
        .map(r -> new RequestSummary(r.getId(), r.getServiceId(), r.getScheduledDate()))
        .toList();
  }

  public Long createRequest(CreateRequestResource resource) {
    var command = new CreateRequestCommand(resource);
    return requestCommandService.handle(command).getId();
  }

  public Long updateRequest(Long requestId, CreateRequestResource resource) {
    var command = new UpdateRequestCommand(requestId, resource);
    return requestCommandService.handle(command).getId();
  }

  public void deleteRequest(Long requestId) {
    var command = new DeleteRequestCommand(requestId);
    requestCommandService.handle(command);
  }

  public record ServiceComponentRequirement(Long componentId, int quantity) {}

  public record RequestSummary(Long requestId, String serviceId, LocalDate scheduledDate) {}
}