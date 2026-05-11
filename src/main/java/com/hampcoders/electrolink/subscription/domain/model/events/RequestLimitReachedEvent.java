package com.hampcoders.electrolink.subscription.domain.model.events;

public record RequestLimitReachedEvent(Long userId, int currentCount, int maxLimit) {
}
