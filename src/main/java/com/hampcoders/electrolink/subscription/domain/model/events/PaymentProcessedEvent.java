package com.hampcoders.electrolink.subscription.domain.model.events;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;

/**
 * Event representing a successful payment processing for a subscription.
 *
 * @param userId The ID of the user who made the payment.
 * @param subscriptionId The ID of the subscription for which the payment was made.
 * @param amount The amount that was paid.
 * @param paymentReference A reference identifier for the payment transaction.
 */
public record PaymentProcessedEvent(Long userId, Long subscriptionId,
                                    Money amount, String paymentReference) {
}
