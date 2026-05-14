package com.hampcoders.electrolink.subscription.domain.services;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.RecordRequestCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.UpgradeSubscriptionCommand;

/**
 * Service interface for handling subscription-related commands.
 */
public interface SubscriptionCommandService {

  /**
   * Handles the creation of a new subscription.
   *
   * @param command The command containing the details for creating a subscription.
   * @return The created Subscription object.
   */
  Subscription handle(CreateSubscriptionCommand command);

  /**
   * Handles the upgrade of an existing subscription.
   *
   * @param command The command containing the details for upgrading a subscription.
   * @return The updated Subscription object after the upgrade.
   */
  Subscription handle(UpgradeSubscriptionCommand command);

  /**
   * Handles the cancellation of an existing subscription.
   *
   * @param command The command containing the details for cancelling a subscription.
   */
  void handle(CancelSubscriptionCommand command);

  /**
   * Handles the recording of a request for a subscription.
   *
   * @param command The command containing the details for recording a request.
   * @return The updated Subscription object after recording the request.
   */
  Subscription handle(RecordRequestCommand command);
}
