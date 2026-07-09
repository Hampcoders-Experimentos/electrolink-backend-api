package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import com.hampcoders.electrolink.sdp.application.internal.services.TechnicianMatchingService;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.services.RequestCommandService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the RequestCommandService interface for handling request-related commands.
 */
@Service
public class RequestCommandServiceImpl implements RequestCommandService {

  private final RequestRepository requestRepository;
  private final TechnicianMatchingService technicianMatchingService;

  /**
   * Constructor for RequestCommandServiceImpl.
   *
   * @param requestRepository the repository for managing request persistence
   * @param technicianMatchingService the service for matching technicians to requests
   */
  public RequestCommandServiceImpl(RequestRepository requestRepository,
                                   TechnicianMatchingService technicianMatchingService) {
    this.requestRepository = requestRepository;
    this.technicianMatchingService = technicianMatchingService;
  }

  @Override
  @Transactional
  public Request handle(CreateRequestCommand command) {
    var userId = Long.parseLong(command.clientId());

    var request = new Request(command);

    if (request.getTechnicianId() == null || request.getTechnicianId().isBlank()) {
      technicianMatchingService.findBestTechnicianForRequest(request)
          .ifPresentOrElse(
              request::assignTechnician,
              () -> {
                throw new IllegalStateException("No available technician found for this request");
              }
          );
    }

    request.registerCreatedEvent();
    var saved = requestRepository.save(request);

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
