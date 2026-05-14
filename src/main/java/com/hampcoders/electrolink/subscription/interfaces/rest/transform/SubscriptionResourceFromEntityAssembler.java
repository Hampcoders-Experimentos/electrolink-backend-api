package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.SubscriptionResource;

/**
 * Assembles a SubscriptionResource from a Subscription entity.
 */
public class SubscriptionResourceFromEntityAssembler {

  /**
   * Transforms a Subscription entity into a SubscriptionResource.
   *
   * @param subscription the Subscription entity to transform
   * @return the transformed SubscriptionResource
   */
  public static SubscriptionResource toResourceFromEntity(Subscription subscription) {
    return new SubscriptionResource(
        subscription.getId(),
        subscription.getUserId(),
        subscription.getPlan().getId(),
        subscription.getPlan().getName().name(),
        subscription.getStatus().name(),
        subscription.getStartDate(),
        subscription.getMonthlyRequestCount(),
        subscription.canMakeRequest()
    );
  }
}
