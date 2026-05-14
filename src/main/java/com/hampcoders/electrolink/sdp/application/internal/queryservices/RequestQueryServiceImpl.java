package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestsByClientIdQuery;
import com.hampcoders.electrolink.sdp.domain.services.RequestQueryService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the RequestQueryService interface
 * that provides methods to handle queries.
 */
@Service
@Transactional(readOnly = true)
public class RequestQueryServiceImpl implements RequestQueryService {

  private final RequestRepository requestRepository;

  /**
   * Constructor for RequestQueryServiceImpl.
   *
   * @param requestRepository the repository used to access request data
   */
  public RequestQueryServiceImpl(RequestRepository requestRepository) {
    this.requestRepository = requestRepository;
  }

  /**
   * Handles the FindRequestByIdQuery to retrieve a request by its ID.
   *
   * @param query The query containing the request ID.
   * @return An Optional containing the Request if found, or empty if not found.
   */
  @Override
  public Optional<Request> handle(FindRequestByIdQuery query) {
    return requestRepository.findById(query.requestId());
  }

  /**
   * Handles the FindRequestsByClientIdQuery to retrieve all requests
   * associated with a specific client ID.
   *
   * @param query The query containing the client ID.
   * @return A list of Requests associated with the given client ID.
   */
  @Override
  public List<Request> handle(FindRequestsByClientIdQuery query) {
    return requestRepository.findByClientId(query.clientId());
  }
}
