package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestsByClientIdQuery;
import com.hampcoders.electrolink.sdp.domain.services.RequestQueryService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class RequestQueryServiceImpl implements RequestQueryService {

  private final RequestRepository requestRepository;

  public RequestQueryServiceImpl(RequestRepository requestRepository) {
    this.requestRepository = requestRepository;
  }

  @Override
  public Optional<Request> handle(FindRequestByIdQuery query) {
    return requestRepository.findById(query.requestId());
  }

  @Override
  public List<Request> handle(FindRequestsByClientIdQuery query) {
    return requestRepository.findByClientId(query.clientId());
  }
}
