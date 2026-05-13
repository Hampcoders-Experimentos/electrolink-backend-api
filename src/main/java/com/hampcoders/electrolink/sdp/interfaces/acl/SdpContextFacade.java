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
    var command = CreateRequestCommandFromResourceAssembler.toCommandFromResource(resource);
    return requestCommandService.handle(command).getId();
  }

  public Long updateRequest(Long requestId, CreateRequestResource resource) {
    var command = UpdateRequestCommandFromResourceAssembler.toCommandFromResource(requestId, resource);
    return requestCommandService.handle(command).getId();
  }

  public void deleteRequest(Long requestId) {
    var command = new DeleteRequestCommand(requestId);
    requestCommandService.handle(command);
  }

  public record ServiceComponentRequirement(Long componentId, int quantity) {}

  public record RequestSummary(Long requestId, String serviceId, LocalDate scheduledDate) {}
}
