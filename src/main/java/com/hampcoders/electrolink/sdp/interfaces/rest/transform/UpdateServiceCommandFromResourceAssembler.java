package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.entities.ComponentQuantity;
import com.hampcoders.electrolink.sdp.domain.model.entities.Tag;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Policy;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Restriction;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.CreateServiceResource;

/**
 * Assembler class that transforms a CreateServiceResource into an UpdateServiceCommand.
 */
public class UpdateServiceCommandFromResourceAssembler {

  /**
   * Converts a CreateServiceResource into an UpdateServiceCommand, using the provided serviceId.
   *
   * @param serviceId the ID of the service to be updated
   * @param resource the CreateServiceResource containing the new service data
   * @return an UpdateServiceCommand with the data from the resource and the provided serviceId
   */
  public static UpdateServiceCommand toCommandFromResource(
      final Long serviceId, final CreateServiceResource resource) {
    var policy = new Policy(
        resource.policy().cancellationPolicy(),
        resource.policy().termsAndConditions());
    var restriction = new Restriction(
        resource.restriction().unavailableDistricts(),
        resource.restriction().forbiddenDays(),
        resource.restriction().requiresSpecialCertification());
    var tags = resource.tags().stream()
        .map(t -> new Tag(t.name()))
        .toList();
    var components = resource.components().stream()
        .map(c -> new ComponentQuantity(c.componentId(), c.quantity()))
        .toList();
    return new UpdateServiceCommand(
        serviceId,
        resource.name(), resource.description(), resource.basePrice(),
        resource.estimatedTime(), resource.category(), resource.isVisible(),
        resource.createdBy(), policy, restriction, tags, components);
  }
}
