package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.CreateSubscriptionResource;

/**
 * Assembler class responsible for transforming a CreateSubscriptionResource
 * into a CreateSubscriptionCommand.
 */
public class CreateSubscriptionCommandFromResourceAssembler {

  /**
   * Converts a CreateSubscriptionResource into a CreateSubscriptionCommand.
   *
   * @param resource The CreateSubscriptionResource containing the data to be transformed.
   * @return A CreateSubscriptionCommand object populated with the data from the resource.
   */
  public static CreateSubscriptionCommand toCommandFromResource(CreateSubscriptionResource
                                                                    resource) {
    return new CreateSubscriptionCommand(resource.userId(), resource.planId());
  }
}
