package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import com.hampcoders.electrolink.subscription.domain.model.commands.CreatePlanCommand;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.CreatePlanResource;

/**
 * Assembler class responsible for converting
 * CreatePlanResource objects into CreatePlanCommand objects.
 */
public class CreatePlanCommandFromResourceAssembler {

  /**
   * Converts a CreatePlanResource object into a CreatePlanCommand object.
   *
   * @param resource the CreatePlanResource object containing the data from the REST request
   * @return a CreatePlanCommand object that can be used by the application layer to create
   */
  public static CreatePlanCommand toCommandFromResource(CreatePlanResource resource) {
    return new CreatePlanCommand(
        resource.name(),
        resource.description(),
        resource.price(),
        resource.maxRequestsPerMonth(),
        resource.prioritySupport()
    );
  }
}
