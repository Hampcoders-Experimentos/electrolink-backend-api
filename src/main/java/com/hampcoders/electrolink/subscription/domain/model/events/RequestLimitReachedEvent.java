package com.hampcoders.electrolink.subscription.domain.model.events;

/**
 * Event triggered when a user reaches the request limit for a subscription plan.
 *
 * @param userId The ID of the user who reached the request limit.
 * @param currentCount The current number of requests made by the user.
 * @param maxLimit The maximum request limit for the user's subscription plan.
 */
public record RequestLimitReachedEvent(Long userId, int currentCount, int maxLimit) {
}
