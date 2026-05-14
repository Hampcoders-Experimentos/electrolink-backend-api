package com.hampcoders.electrolink.monitoring.application.internal.queryservices;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetAllServiceOperationsQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetServiceOperationByIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetServiceOperationsByTechnicianIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.domain.services.ServiceOperationQueryService;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the query service for ServiceOperation entities.
 */
@Service
public class ServiceOperationQueryServiceImpl implements ServiceOperationQueryService {

  private final ServiceOperationRepository serviceOperationRepository;

  /**
   * Constructor for ServiceOperationQueryServiceImpl.
   *
   * @param serviceOperationRepository The repository for accessing ServiceOperation data.
   */
  public ServiceOperationQueryServiceImpl(ServiceOperationRepository serviceOperationRepository) {
    this.serviceOperationRepository = serviceOperationRepository;
  }

  /**
   * Handles the query to retrieve all service operations.
   *
   * @param query The query object (placeholder).
   * @return A list of all ServiceOperation entities.
   */
  @Override
  public List<ServiceOperation> handle(GetAllServiceOperationsQuery query) {
    return serviceOperationRepository.findAll();
  }

  /**
   * Handles the query to retrieve a service operation by its request ID.
   *
   * @param query The query object containing the request ID.
   * @return An Optional containing the ServiceOperation entity, or empty if not found.
   */
  @Override
  public Optional<ServiceOperation> handle(GetServiceOperationByIdQuery query) {
    return serviceOperationRepository.findById(query.serviceOperationId());
  }

  /**
   * Handles the query to retrieve all service operations associated with a specific technician ID.
   *
   * @param query The query object containing the technician ID.
   * @return A list of ServiceOperation entities matching the technician ID.
   */
  @Override
  public List<ServiceOperation> handle(GetServiceOperationsByTechnicianIdQuery query) {
    return serviceOperationRepository.findByTechnicianId(new TechnicianId(query.technicianId()));
  }
}