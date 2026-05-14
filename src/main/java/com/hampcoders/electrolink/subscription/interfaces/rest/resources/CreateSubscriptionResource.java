package com.hampcoders.electrolink.subscription.interfaces.rest.resources;

/**
 * Resource class representing the data needed to create a new subscription for a user.
 *
 * @param userId The ID of the user who is subscribing to a plan.
 * @param planId The ID of the subscription plan that the user is subscribing to.
 */
public record CreateSubscriptionResource(Long userId, Long planId) {
}
