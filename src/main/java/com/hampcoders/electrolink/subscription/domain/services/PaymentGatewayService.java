package com.hampcoders.electrolink.subscription.domain.services;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;

public interface PaymentGatewayService {
    String createPayment(Long userId, Money amount, String description);
    boolean verifyPayment(String paymentReference);
}
