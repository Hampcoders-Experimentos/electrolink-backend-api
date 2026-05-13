package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import com.hampcoders.electrolink.sdp.application.internal.services.TechnicianMatchingService;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.services.RequestCommandService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
import com.hampcoders.electrolink.subscription.interfaces.acl.SubscriptionContextFacade;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RequestCommandServiceImpl implements RequestCommandService {

  private final RequestRepository requestRepository;
  private final SubscriptionContextFacade subscriptionContextFacade;
  private final TechnicianMatchingService technicianMatchingService;

  public RequestCommandServiceImpl(RequestRepository requestRepository,
                                   SubscriptionContextFacade subscriptionContextFacade,
                                   TechnicianMatchingService technicianMatchingService) {
    this.requestRepository = requestRepository;
    this.subscriptionContextFacade = subscriptionContextFacade;
    this.technicianMatchingService = technicianMatchingService;
  }

  @Override
  @Transactional
  public Request handle(CreateRequestCommand command) {
    var userId = Long.parseLong(command.clientId());

    if (!subscriptionContextFacade.canUserMakeRequest(userId)) {
      throw new IllegalStateException("Monthly request limit reached. Please upgrade your plan.");
    }

    var request = new Request(command);

    if (request.getTechnicianId() == null || request.getTechnicianId().isBlank()) {
      technicianMatchingService.findBestTechnicianForRequest(request)
          .ifPresentOrElse(
              request::assignTechnician,
              () -> { throw new IllegalStateException("No available technician found for this request"); }
          );
    }

    request.registerCreatedEvent();
    var saved = requestRepository.save(request);

    subscriptionContextFacade.recordRequest(userId);

    return saved;
  }

  @Override
  @Transactional
  public Request handle(UpdateRequestCommand command) {
    return requestRepository.findById(command.requestId())
        .map(existing -> {
          existing.updateFrom(command);
          return requestRepository.save(existing);
        })
        .orElseThrow(() -> new IllegalArgumentException("Request not found"));
  }

  @Override
  @Transactional
  public void handle(DeleteRequestCommand command) {
    var request = requestRepository.findById(command.requestId())
        .orElseThrow(() -> new IllegalArgumentException("Request not found"));
    requestRepository.delete(request);
  }
}
