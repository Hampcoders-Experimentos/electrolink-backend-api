package com.hampcoders.electrolink.subscription.interfaces.rest.resources;

import java.time.LocalDateTime;

/**
 * SubscriptionResource is a record that represents the subscription details of a user.
 *
 * @param id the unique identifier of the subscription
 * @param userId the unique identifier of the user associated with the subscription
 * @param planId the unique identifier of the subscription plan
 * @param planName the name of the subscription plan
 * @param status the current status of the subscription
 * @param startDate the date and time when the subscription started
 * @param monthlyRequestCount the number of requests made by the user in the current month
 * @param canMakeRequest a boolean indicating whether the user
 *      can make a request based on their subscription status and request count
 */
public record SubscriptionResource(Long id, Long userId, Long planId, String planName,
                                   String status, LocalDateTime startDate,
                                   int monthlyRequestCount, boolean canMakeRequest) {
}
