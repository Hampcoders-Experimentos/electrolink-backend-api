package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindServiceByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.GetAllServicesQuery;
import com.hampcoders.electrolink.sdp.domain.services.ServiceQueryService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ServiceRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the ServiceQueryService interface
 * that handles queries related to ServiceEntity.
 */
@Service
@Transactional(readOnly = true)
public class ServiceQueryServiceImpl implements ServiceQueryService {

  private final ServiceRepository serviceRepository;

  /**
   * Constructor for ServiceQueryServiceImpl.
   *
   * @param serviceRepository the repository used to access ServiceEntity data
   */
  public ServiceQueryServiceImpl(ServiceRepository serviceRepository) {
    this.serviceRepository = serviceRepository;
  }

  /**
   * Handles the FindServiceByIdQuery by retrieving the ServiceEntity
   * with the specified ID from the repository.
   *
   * @param query The query containing the service ID.
   * @return An Optional containing the ServiceEntity if found, or empty if not found.
   */
  @Override
  public Optional<ServiceEntity> handle(FindServiceByIdQuery query) {
    return serviceRepository.findById(query.serviceId());
  }

  /**
   * Handles the GetAllServicesQuery by retrieving all ServiceEntity instances from the repository.
   *
   * @param query The query to get all services.
   * @return A list of all ServiceEntity instances.
   */
  @Override
  public List<ServiceEntity> handle(GetAllServicesQuery query) {
    return serviceRepository.findAll();
  }
}
