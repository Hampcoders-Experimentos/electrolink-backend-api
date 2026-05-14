package com.hampcoders.electrolink.subscription.application.internal.eventhandlers;

import com.hampcoders.electrolink.subscription.domain.model.events.SubscriptionActivatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * SubscriptionEventPublisher is responsible for
 * handling subscription-related events and publishing them to the appropriate channels.
 */
@Component
public class SubscriptionEventPublisher {

  private static final Logger log = LoggerFactory.getLogger(SubscriptionEventPublisher.class);

  /**
   * Handles the SubscriptionActivatedEvent and logs the activation details.
   *
   * @param event the SubscriptionActivatedEvent containing details about the activated subscription
   */
  @EventListener
  public void onSubscriptionActivated(SubscriptionActivatedEvent event) {
    log.info("Subscription activated for userId: {}, planId: {}, plan: {}",
        event.userId(), event.planId(), event.planName());
  }
}
