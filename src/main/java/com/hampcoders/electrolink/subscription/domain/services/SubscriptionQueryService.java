package com.hampcoders.electrolink.subscription.domain.services;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetActiveSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetSubscriptionByUserIdQuery;
import java.util.Optional;

/**
 * Service interface for handling subscription-related queries.
 */
public interface SubscriptionQueryService {
  /**
   * Handles the GetSubscriptionByUserIdQuery to retrieve a subscription by user ID.
   *
   * @param query The query containing the user ID for which to retrieve the subscription.
   * @return An Optional containing the Subscription if found, or empty if not found.
   */
  Optional<Subscription> handle(GetSubscriptionByUserIdQuery query);

  /**
   * Handles the GetActiveSubscriptionByUserIdQuery to retrieve an active subscription by user ID.
   *
   * @param query The query containing the user ID for which to retrieve the active subscription.
   * @return An Optional containing the Subscription if found, or empty if not found.
   */
  Optional<Subscription> handle(GetActiveSubscriptionByUserIdQuery query);

  /**
   * Checks if a user can make a request based on their subscription status.
   *
   * @param userId The ID of the user for whom to check the subscription status.
   * @return true if the user can make a request, false otherwise.
   */
  boolean canUserMakeRequest(Long userId);
}
