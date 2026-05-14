package com.hampcoders.electrolink.subscription.domain.model.events;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;

/**
 * Event representing the activation of a subscription for a user.
 *
 * @param userId The ID of the user for whom the subscription was activated.
 * @param planId The ID of the subscription plan that was activated.
 * @param planName The name of the subscription plan that was activated.
 */
public record SubscriptionActivatedEvent(Long userId, Long planId, PlanType planName) {
}
