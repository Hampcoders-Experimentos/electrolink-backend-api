package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.commands.CreateServiceOperationCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.UpdateServiceStatusCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.services.ServiceOperationCommandService;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the command service for ServiceOperation entities.
 */
@Service
@Transactional
public class ServiceOperationCommandServiceImpl implements ServiceOperationCommandService {

  private final ServiceOperationRepository serviceOperationRepository;

  public ServiceOperationCommandServiceImpl(ServiceOperationRepository serviceOperationRepository) {
    this.serviceOperationRepository = serviceOperationRepository;
  }

  /**
   * Handles the creation of a new service operation.
   *
   * @param command The command containing the details for the new service operation.
   * @return The RequestId of the created service operation.
   */
  @Override
  public Long handle(CreateServiceOperationCommand command) {
    var serviceOperation = new ServiceOperation(command);

    serviceOperationRepository.save(serviceOperation);
    return serviceOperation.getId();
  }

  /**
   * Handles the update of a service operation's status.
   *
   * @param command The command containing the ID and the new status.
   */
  @Override
  public void handle(UpdateServiceStatusCommand command) {
    var serviceOperation = serviceOperationRepository
            .findById(command.serviceOperationId())
            .orElseThrow(() -> new IllegalArgumentException("ServiceOperation not found"));

    ServiceStatus newStatus = ServiceStatus.valueOf(command.newStatus());
    serviceOperation.updateStatus(newStatus);

    serviceOperationRepository.save(serviceOperation);
  }
}