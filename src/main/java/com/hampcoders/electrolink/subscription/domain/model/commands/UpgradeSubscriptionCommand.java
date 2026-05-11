package com.hampcoders.electrolink.subscription.domain.model.commands;

public record UpgradeSubscriptionCommand(Long userId, Long newPlanId) {
}
