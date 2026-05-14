package com.hampcoders.electrolink.subscription.domain.model.queries;

/**
 * Query object representing the data needed to retrieve a subscription plan by its ID.
 *
 * @param planId The ID of the subscription plan being queried.
 */
public record GetPlanByIdQuery(Long planId) {
}
