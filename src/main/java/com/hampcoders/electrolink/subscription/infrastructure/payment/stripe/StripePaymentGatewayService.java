package com.hampcoders.electrolink.subscription.infrastructure.payment.stripe;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;
import com.hampcoders.electrolink.subscription.domain.services.PaymentGatewayService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentGatewayService implements PaymentGatewayService {

    @Override
    public String createPayment(Long userId, Money amount, String description) {
        String paymentReference = "stripe_" + UUID.randomUUID().toString();
        return paymentReference;
    }

    @Override
    public boolean verifyPayment(String paymentReference) {
        return true;
    }
}
