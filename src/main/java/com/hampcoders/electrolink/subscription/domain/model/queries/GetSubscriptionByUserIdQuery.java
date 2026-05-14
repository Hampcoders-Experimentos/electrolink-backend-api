package com.hampcoders.electrolink.subscription.domain.model.queries;

/**
 * Query object representing the data needed to retrieve a subscription for a user by their ID.
 *
 * @param userId The ID of the user whose subscription is being queried.
 */
public record GetSubscriptionByUserIdQuery(Long userId) {
}
