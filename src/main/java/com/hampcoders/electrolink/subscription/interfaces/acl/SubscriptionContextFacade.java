package com.hampcoders.electrolink.subscription.interfaces.acl;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.RecordRequestCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.UpgradeSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetActiveSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionCommandService;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionQueryService;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Facade for subscription-related operations,
 * providing a simplified interface for controllers and other clients.
 */
@Service
public class SubscriptionContextFacade {

  private final SubscriptionCommandService subscriptionCommandService;
  private final SubscriptionQueryService subscriptionQueryService;

  /**
   * Constructor for SubscriptionContextFacade.
   *
   * @param subscriptionCommandService the service for handling subscription commands
   * @param subscriptionQueryService the service for handling subscription queries
   */
  public SubscriptionContextFacade(SubscriptionCommandService subscriptionCommandService,
                                   SubscriptionQueryService subscriptionQueryService) {
    this.subscriptionCommandService = subscriptionCommandService;
    this.subscriptionQueryService = subscriptionQueryService;
  }

  /**
   * Creates a new subscription for a user with the specified plan.
   *
   * @param userId the ID of the user
   * @param planId the ID of the subscription plan
   * @return the ID of the newly created subscription
   */
  public Long createSubscription(Long userId, Long planId) {
    var command = new CreateSubscriptionCommand(userId, planId);
    return subscriptionCommandService.handle(command).getId();
  }

  /**
   * Upgrades the user's subscription to a new plan.
   *
   * @param userId the ID of the user
   * @param newPlanId the ID of the new subscription plan
   */
  public void upgradeSubscription(Long userId, Long newPlanId) {
    subscriptionCommandService.handle(new UpgradeSubscriptionCommand(userId, newPlanId));
  }

  /**
   * Cancels the user's active subscription.
   *
   * @param userId the ID of the user
   */
  public void cancelSubscription(Long userId) {
    subscriptionCommandService.handle(new CancelSubscriptionCommand(userId));
  }

  /**
   * Checks if the user can make a request based on their subscription status and limits.
   *
   * @param userId the ID of the user
   * @return true if the user can make a request, false otherwise
   */
  public boolean canUserMakeRequest(Long userId) {
    return subscriptionQueryService.canUserMakeRequest(userId);
  }

  /**
   * Records a request made by the user, updating their subscription usage accordingly.
   *
   * @param userId the ID of the user
   */
  public void recordRequest(Long userId) {
    subscriptionCommandService.handle(new RecordRequestCommand(userId));
  }

  /**
   * Retrieves the active subscription for a user, if it exists.
   *
   * @param userId the ID of the user
   * @return an Optional containing the active subscription, or empty if none exists for the user
   */
  public Optional<Subscription> getActiveSubscription(Long userId) {
    return subscriptionQueryService.handle(new GetActiveSubscriptionByUserIdQuery(userId));
  }

  /**
   * Retrieves the ID of the active subscription plan for a user, if it exists.
   *
   * @param userId the ID of the user
   * @return an Optional containing the ID of the active subscription plan,
   *     or empty if none exists for the user
   */
  public Optional<Long> getActiveSubscriptionPlanId(Long userId) {
    return subscriptionQueryService.handle(new GetActiveSubscriptionByUserIdQuery(userId))
        .map(s -> s.getPlan().getId());
  }

  /**
   * Checks if the user has an active premium subscription.
   *
   * @param userId the ID of the user
   * @return true if the user has an active premium subscription, false otherwise
   */
  public boolean isPremiumUser(Long userId) {
    return subscriptionQueryService.handle(new GetActiveSubscriptionByUserIdQuery(userId))
        .map(Subscription::isPremium)
        .orElse(false);
  }
}
