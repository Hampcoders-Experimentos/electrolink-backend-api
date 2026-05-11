package com.hampcoders.electrolink.subscription.domain.model.events;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;

public record SubscriptionActivatedEvent(Long userId, Long planId, PlanType planName) {
}
