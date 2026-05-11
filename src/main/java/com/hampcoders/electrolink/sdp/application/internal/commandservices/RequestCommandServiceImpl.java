package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import com.hampcoders.electrolink.sdp.application.internal.services.TechnicianMatchingService;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.services.RequestCommandService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.RequestMapper;
import com.hampcoders.electrolink.subscription.interfaces.acl.SubscriptionContextFacade;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the RequestCommandService interface.
 * This service handles commands related to creating, updating, and deleting Request aggregates.
 */
@Service
public class RequestCommandServiceImpl implements RequestCommandService {

  private final RequestRepository requestRepository;
  private final SubscriptionContextFacade subscriptionContextFacade;
  private final TechnicianMatchingService technicianMatchingService;

  /**
   * Constructs a new RequestCommandServiceImpl with the given dependencies.
   *
   * @param requestRepository         The repository for accessing request data.
   *
   * @param subscriptionContextFacade The facade for subscription validation.
   *
   * @param technicianMatchingService The service for auto-assigning technicians.
   */
  public RequestCommandServiceImpl(RequestRepository requestRepository,
                                   SubscriptionContextFacade subscriptionContextFacade,
                                   TechnicianMatchingService technicianMatchingService) {
    this.requestRepository = requestRepository;
    this.subscriptionContextFacade = subscriptionContextFacade;
    this.technicianMatchingService = technicianMatchingService;
  }

  /**
   * Handles the CreateRequestCommand to create a new request.
   *
   * @param command The command containing the data for the new request.
   *
   * @return The created Request aggregate.
   */
  @Override
  @Transactional
  public Request handle(CreateRequestCommand command) {
    var userId = Long.parseLong(command.resource().clientId());

    if (!subscriptionContextFacade.canUserMakeRequest(userId)) {
      throw new IllegalStateException("Monthly request limit reached. Please upgrade your plan.");
    }

    var request = RequestMapper.toModel(command.resource());

    if (request.getTechnicianId() == null || request.getTechnicianId().isBlank()) {
      technicianMatchingService.findBestTechnicianForRequest(request)
          .ifPresentOrElse(
              request::assignTechnician,
              () -> { throw new IllegalStateException("No available technician found for this request"); }
          );
    }

    var saved = requestRepository.save(request);
    saved.registerCreatedEvent();
    var result = requestRepository.save(saved);

    subscriptionContextFacade.recordRequest(userId);

    return result;
  }

  /**
   * Handles the UpdateRequestCommand to update an existing request.
   *
   * @param command The command containing the updated data for the request.
   *
   * @return The updated Request aggregate.
   *
   * @throws IllegalArgumentException if the request is not found.
   */
  @Override
  @Transactional
  public Request handle(UpdateRequestCommand command) {
    return requestRepository.findById(command.requestId())
        .map(existing -> {
          existing.updateFrom(command.resource());
          return requestRepository.save(existing);
        })
        .orElseThrow(() -> new IllegalArgumentException("Request not found"));
  }

  /**
   * Handles the DeleteRequestCommand to delete a request.
   *
   * @param command The command containing the ID of the request to delete.
   *
   * @throws IllegalArgumentException if the request is not found.
   */
  @Override
  @Transactional
  public void handle(DeleteRequestCommand command) {
    var request = requestRepository.findById(command.requestId())
        .orElseThrow(() -> new IllegalArgumentException("Request not found"));
    requestRepository.delete(request);
  }
}
