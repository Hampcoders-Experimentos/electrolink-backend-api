package com.hampcoders.electrolink.subscription.domain.model.events;

public record SubscriptionCancelledEvent(Long userId, Long planId) {
}
