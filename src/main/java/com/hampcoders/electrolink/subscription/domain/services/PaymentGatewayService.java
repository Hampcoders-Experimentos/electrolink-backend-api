package com.hampcoders.electrolink.subscription.domain.services;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;

/**
 * Interface for payment gateway services.
 */
public interface PaymentGatewayService {

  /**
   * Creates a payment for the specified user, amount, and description.
   *
   * @param userId The ID of the user for whom to create the payment.
   * @param amount The amount to be paid.
   * @param description A description of the payment.
   * @return The reference for the created payment.
   */
  String createPayment(Long userId, Money amount, String description);

  /**
   * Verifies the payment with the given reference.
   *
   * @param paymentReference The reference of the payment to verify.
   * @return true if the payment is verified successfully, false otherwise.
   */
  boolean verifyPayment(String paymentReference);
}
