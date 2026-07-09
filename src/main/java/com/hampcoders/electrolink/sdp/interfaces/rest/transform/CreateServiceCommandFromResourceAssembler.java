package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.commands.CreateServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.entities.ComponentQuantity;
import com.hampcoders.electrolink.sdp.domain.model.entities.Tag;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Policy;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Restriction;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateServiceResource;

/**
 * Assembler class responsible for transforming a CreateServiceResource into a CreateServiceCommand.
 */
public class CreateServiceCommandFromResourceAssembler {

  /**
   * Converts a CreateServiceResource into a CreateServiceCommand.
   *
   * @param resource The CreateServiceResource containing the data to be transformed.
   * @return A CreateServiceCommand object populated with the data from the resource.
   */
  public static CreateServiceCommand toCommandFromResource(final CreateServiceResource resource) {
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
    return new CreateServiceCommand(
        resource.name(), resource.description(), resource.basePrice(),
        resource.estimatedTime(), resource.category(), resource.isVisible(),
        resource.createdBy(), policy, restriction, tags, components);
  }
}
