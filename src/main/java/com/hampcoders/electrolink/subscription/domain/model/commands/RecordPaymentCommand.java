package com.hampcoders.electrolink.subscription.domain.model.commands;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;

/**
 * Command to record a payment for a subscription.
 *
 * @param userId The ID of the user making the payment.
 * @param subscriptionId The ID of the subscription for which the payment is being recorded.
 * @param amount The amount of the payment.
 * @param paymentGatewayReference The reference provided by the payment gateway.
 * @param status The status of the payment.
 */
public record RecordPaymentCommand(Long userId, Long subscriptionId, Money amount,
                                   String paymentGatewayReference, String status) {
}
