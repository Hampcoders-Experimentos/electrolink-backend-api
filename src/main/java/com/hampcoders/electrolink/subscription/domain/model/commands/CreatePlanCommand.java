package com.hampcoders.electrolink.subscription.domain.model.commands;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;

/**
 * Command object representing the data needed to create a new subscription plan.
 *
 * @param name the name of the subscription plan, represented as a PlanType enum
 * @param description a brief description of the subscription plan
 * @param price the monthly price of the subscription plan
 * @param maxRequestsPerMonth the maximum number of service requests allowed per month
 * @param prioritySupport a flag indicating whether the subscription plan includes priority support
 */
public record CreatePlanCommand(PlanType name, String description, double price,
                                int maxRequestsPerMonth, boolean prioritySupport) {
}
