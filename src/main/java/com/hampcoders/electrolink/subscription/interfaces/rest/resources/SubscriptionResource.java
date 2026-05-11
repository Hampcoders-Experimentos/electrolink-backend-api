package com.hampcoders.electrolink.subscription.interfaces.rest.resources;

import java.time.LocalDateTime;

public record SubscriptionResource(Long id, Long userId, Long planId, String planName,
                                   String status, LocalDateTime startDate,
                                   int monthlyRequestCount, boolean canMakeRequest) {
}
