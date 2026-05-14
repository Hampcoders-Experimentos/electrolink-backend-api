package com.hampcoders.electrolink.subscription.domain.model.commands;

/**
 * Command to upgrade a user's subscription plan to a new plan.
 *
 * @param userId the user id
 * @param newPlanId the id of the new plan
 */
public record UpgradeSubscriptionCommand(Long userId, Long newPlanId) {
}
