package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateServiceCommand;
import com.hampcoders.electrolink.sdp.domain.services.ServiceCommandService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ServiceCommandService} interface.
 * This service handles commands related to service entities.
 */
@Service
public class ServiceCommandServiceImpl implements ServiceCommandService {

  private final ServiceRepository serviceRepository;

  /**
   * Constructor for ServiceCommandServiceImpl.
   *
   * @param serviceRepository The repository for service entities.
   */
  public ServiceCommandServiceImpl(ServiceRepository serviceRepository) {
    this.serviceRepository = serviceRepository;
  }

  /**
   * Handles the {@link CreateServiceCommand}.
   *
   * @param command The command to create a service.
   * @return The ID of the created service.
   */
  @Override
  @Transactional
  public Long handle(CreateServiceCommand command) {
    var service = new ServiceEntity(
        command.name(),
        command.description(),
        command.price(),
        command.estimatedTime(),
        command.category(),
        command.isVisible(),
        command.createdBy(),
        command.policy(),
        command.restriction(),
        command.tags(),
        command.components()
    );
    var saved = serviceRepository.save(service);
    saved.registerCreatedEvent();
    serviceRepository.save(saved);
    return saved.getId();
  }

  /**
   * Handles the {@link UpdateServiceCommand}.
   *
   * @param command The command to update a service.
   */
  @Override
  @Transactional
  public void handle(UpdateServiceCommand command) {
    var existing = serviceRepository.findById(command.serviceId())
        .orElseThrow(() -> new IllegalArgumentException("Service not found with id: "
            + command.serviceId()));

    var updated = new ServiceEntity(
        command.name(),
        command.description(),
        command.price(),
        command.estimatedTime(),
        command.category(),
        command.isVisible(),
        command.createdBy(),
        command.policy(),
        command.restriction(),
        command.tags(),
        command.components()
    );

    existing.updateFrom(updated);
    serviceRepository.save(existing);
  }


  /**
   * Handles the {@link DeleteServiceCommand}.
   *
   * @param command The command to delete a service.
   */
  @Override
  @Transactional
  public void handle(DeleteServiceCommand command) {
    if (!serviceRepository.existsById(command.serviceId())) {
      throw new IllegalArgumentException("Service not found with id: "
          + command.serviceId());
    }
    serviceRepository.deleteById(command.serviceId());
  }
}