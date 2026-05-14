package com.hampcoders.electrolink.subscription.application.internal.queryservices;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetActiveSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.SubscriptionStatus;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionQueryService;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the SubscriptionQueryService interface
 * that provides methods to query subscription data.
 */
@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

  private final SubscriptionRepository subscriptionRepository;

  /**
   * Constructor for SubscriptionQueryServiceImpl.
   *
   * @param subscriptionRepository the repository used to access subscription data
   */
  public SubscriptionQueryServiceImpl(SubscriptionRepository subscriptionRepository) {
    this.subscriptionRepository = subscriptionRepository;
  }

  /**
   * Handles the GetSubscriptionByUserIdQuery
   * by retrieving the subscription associated with the given user ID.
   *
   * @param query the query containing the user ID for which to retrieve the subscription
   * @return an Optional containing the Subscription if found, or empty if not found
   */
  @Override
  public Optional<Subscription> handle(GetSubscriptionByUserIdQuery query) {
    return subscriptionRepository.findByUserId(query.userId());
  }

  /**
   * Handles the GetActiveSubscriptionByUserIdQuery
   * by retrieving the active subscription associated with the given user ID.
   *
   * @param query the query containing the user ID for which to retrieve the active subscription
   * @return an Optional containing the active Subscription if found, or empty if not found
   */
  @Override
  public Optional<Subscription> handle(GetActiveSubscriptionByUserIdQuery query) {
    return subscriptionRepository.findByUserIdAndStatus(query.userId(), SubscriptionStatus.ACTIVE);
  }

  /**
   * Checks if a user can make a request based on their subscription status.
   *
   * @param userId the ID of the user for which to check if they can make a request
   * @return true if the user can make a request, false otherwise
   */
  @Override
  public boolean canUserMakeRequest(Long userId) {
    return subscriptionRepository.findByUserId(userId)
        .map(Subscription::canMakeRequest)
        .orElse(false);
  }
}
