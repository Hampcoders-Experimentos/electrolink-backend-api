package com.hampcoders.electrolink.subscription.domain.model.events;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;

public record PaymentProcessedEvent(Long userId, Long subscriptionId, Money amount, String paymentReference) {
}
