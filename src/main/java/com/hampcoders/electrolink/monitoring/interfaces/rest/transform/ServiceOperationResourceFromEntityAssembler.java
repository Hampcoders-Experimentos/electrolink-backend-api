package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.ServiceOperationResource;

/**
 * Assembler responsible for converting {@link ServiceOperation} entities
 * into {@link ServiceOperationResource} objects.
 */
public class ServiceOperationResourceFromEntityAssembler {

  /**
   * Converts a ServiceOperation entity into a ServiceOperationResource.
   *
   * @param entity The ServiceOperation entity.
   * @return The corresponding ServiceOperationResource.
   */
  public static ServiceOperationResource toResourceFromEntity(ServiceOperation entity) {
    return new ServiceOperationResource(
        entity.getId(),
        entity.getRequestId().requestId(),
        entity.getTechnicianId().technicianId(),
        entity.getStartedAt(),
        entity.getCompletedAt(),
        entity.getCurrentStatus().name()
    );
  }
}