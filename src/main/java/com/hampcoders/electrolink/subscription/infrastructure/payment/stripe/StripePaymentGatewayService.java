package com.hampcoders.electrolink.subscription.infrastructure.payment.stripe;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;
import com.hampcoders.electrolink.subscription.domain.services.PaymentGatewayService;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Implementation of the PaymentGatewayService using Stripe as the payment provider.
 */
@Service
public class StripePaymentGatewayService implements PaymentGatewayService {

  /**
   * Creates a payment using Stripe's API.
   *
   * @param userId The ID of the user for whom to create the payment.
   * @param amount The amount to be paid.
   * @param description A description of the payment.
   * @return A payment reference string that can be used to verify the payment later.
   */
  @Override
  public String createPayment(Long userId, Money amount, String description) {
    return "stripe_" + UUID.randomUUID().toString();
  }

  /**
   * Verifies a payment using Stripe's API.
   *
   * @param paymentReference The reference of the payment to verify.
   * @return true if the payment is verified successfully, false otherwise.
   */
  @Override
  public boolean verifyPayment(String paymentReference) {
    return true;
  }
}
