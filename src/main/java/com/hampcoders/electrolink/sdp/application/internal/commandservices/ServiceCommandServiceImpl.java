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
 * Implementation of the ServiceCommandService interface
 * that handles commands related to ServiceEntity.
 */
@Service
public class ServiceCommandServiceImpl implements ServiceCommandService {

  private final ServiceRepository serviceRepository;

  /**
   * Constructor for ServiceCommandServiceImpl.
   *
   * @param serviceRepository the repository for managing ServiceEntity persistence and retrieval
   */
  public ServiceCommandServiceImpl(ServiceRepository serviceRepository) {
    this.serviceRepository = serviceRepository;
  }

  @Override
  @Transactional
  public Long handle(CreateServiceCommand command) {
    var service = new ServiceEntity(command);
    service.registerCreatedEvent();
    var saved = serviceRepository.save(service);
    return saved.getId();
  }

  @Override
  @Transactional
  public void handle(UpdateServiceCommand command) {
    var existing = serviceRepository.findById(command.serviceId())
        .orElseThrow(() -> new IllegalArgumentException("Service not found with id: "
            + command.serviceId()));
    existing.updateFrom(command);
    serviceRepository.save(existing);
  }

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
