package com.hampcoders.electrolink.subscription.domain.model.queries;

/**
 * Query object representing the data needed to retrieve
 * the active subscription for a user by their ID.
 *
 * @param userId The ID of the user whose active subscription is being queried.
 */
public record GetActiveSubscriptionByUserIdQuery(Long userId) {
}
