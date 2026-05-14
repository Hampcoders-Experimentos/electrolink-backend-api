package com.hampcoders.electrolink.subscription.interfaces.rest.resources;

/**
 * PlanResource is a record that represents the data structure for a subscription plan.
 *
 * @param id The unique identifier of the subscription plan.
 * @param name The name of the subscription plan.
 * @param description A brief description of the subscription plan.
 * @param price The price of the subscription plan.
 * @param maxRequestsPerMonth The maximum number of requests allowed per month for this plan.
 * @param prioritySupport Indicates whether the subscription plan includes priority support.
 * @param isActive Indicates whether the subscription plan is currently active.
 */
public record PlanResource(Long id, String name, String description, double price,
                           int maxRequestsPerMonth, boolean prioritySupport, boolean isActive) {
}
