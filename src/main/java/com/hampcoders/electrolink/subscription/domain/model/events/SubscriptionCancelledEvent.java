package com.hampcoders.electrolink.subscription.domain.model.events;

/**
 * Event representing the cancellation of a subscription.
 *
 * @param userId The ID of the user whose subscription was canceled.
 * @param planId The ID of the subscription plan that was canceled.
 */
public record SubscriptionCancelledEvent(Long userId, Long planId) {
}
