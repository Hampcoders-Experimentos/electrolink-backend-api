package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindServiceByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.GetAllServicesQuery;
import com.hampcoders.electrolink.sdp.domain.services.ServiceQueryService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ServiceRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class ServiceQueryServiceImpl implements ServiceQueryService {

  private final ServiceRepository serviceRepository;

  public ServiceQueryServiceImpl(ServiceRepository serviceRepository) {
    this.serviceRepository = serviceRepository;
  }

  @Override
  public Optional<ServiceEntity> handle(FindServiceByIdQuery query) {
    return serviceRepository.findById(query.serviceId());
  }

  @Override
  public List<ServiceEntity> handle(GetAllServicesQuery query) {
    return serviceRepository.findAll();
  }
}
