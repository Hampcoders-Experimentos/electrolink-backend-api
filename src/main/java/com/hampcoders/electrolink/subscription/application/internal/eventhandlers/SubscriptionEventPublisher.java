package com.hampcoders.electrolink.subscription.application.internal.eventhandlers;

import com.hampcoders.electrolink.subscription.domain.model.events.SubscriptionActivatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionEventPublisher.class);

    @EventListener
    public void onSubscriptionActivated(SubscriptionActivatedEvent event) {
        log.info("Subscription activated for userId: {}, planId: {}, plan: {}",
            event.userId(), event.planId(), event.planName());
    }
}
