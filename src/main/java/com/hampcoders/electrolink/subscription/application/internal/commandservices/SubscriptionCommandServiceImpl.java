package com.hampcoders.electrolink.subscription.application.internal.commandservices;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.RecordRequestCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.UpgradeSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionCommandService;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementation of SubscriptionCommandService that handles subscription-related commands.
 */
@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

  private final SubscriptionRepository subscriptionRepository;
  private final PlanRepository planRepository;

  /**
   * Constructor for SubscriptionCommandServiceImpl.
   *
   * @param subscriptionRepository Repository for managing subscriptions.
   * @param planRepository Repository for managing plans.
   */
  public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository,
                                        PlanRepository planRepository) {
    this.subscriptionRepository = subscriptionRepository;
    this.planRepository = planRepository;
  }

  /**
   * Handles the creation of a new subscription. If the user already has a subscription,
   * it upgrades it to the new plan.
   *
   * @param command The command containing the details for creating or upgrading a subscription.
   * @return The created or upgraded subscription.
   */
  @Override
  @Transactional
  public Subscription handle(CreateSubscriptionCommand command) {
    var plan = planRepository.findById(command.planId())
        .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + command.planId()));

    var existing = subscriptionRepository.findByUserId(command.userId());
    if (existing.isPresent()) {
      var sub = existing.get();
      sub.upgradeTo(plan);
      return subscriptionRepository.save(sub);
    }

    var subscription = new Subscription(command, plan);
    return subscriptionRepository.save(subscription);
  }

  /**
   * Handles the upgrade of an existing subscription to a new plan.
   *
   * @param command The command containing the details for upgrading a subscription.
   * @return The upgraded subscription.
   */
  @Override
  @Transactional
  public Subscription handle(UpgradeSubscriptionCommand command) {
    var newPlan = planRepository.findById(command.newPlanId())
        .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + command.newPlanId()));

    var subscription = subscriptionRepository.findByUserId(command.userId())
        .orElseThrow(() -> new IllegalArgumentException("No subscription found for user: "
            + command.userId()));

    subscription.upgradeTo(newPlan);
    return subscriptionRepository.save(subscription);
  }

  /**
   * Handles the cancellation of an existing subscription.
   *
   * @param command The command containing the details for canceling a subscription.
   */
  @Override
  @Transactional
  public void handle(CancelSubscriptionCommand command) {
    var subscription = subscriptionRepository.findByUserId(command.userId())
        .orElseThrow(() -> new IllegalArgumentException("No subscription found for user: "
            + command.userId()));

    subscription.cancel();
    subscriptionRepository.save(subscription);
  }

  /**
   * Handles the recording of a request for an existing subscription.
   *
   * @param command The command containing the details for recording a request.
   * @return The updated subscription.
   */
  @Override
  @Transactional
  public Subscription handle(RecordRequestCommand command) {
    var subscription = subscriptionRepository.findByUserId(command.userId())
        .orElseThrow(() -> new IllegalArgumentException(
            "No subscription found for user: " + command.userId()));
    subscription.recordRequest();
    return subscriptionRepository.save(subscription);
  }
}
