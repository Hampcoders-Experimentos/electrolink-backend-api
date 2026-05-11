package com.hampcoders.electrolink.subscription.domain.model.commands;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;

public record RecordPaymentCommand(Long userId, Long subscriptionId, Money amount,
                                   String paymentGatewayReference, String status) {
}
