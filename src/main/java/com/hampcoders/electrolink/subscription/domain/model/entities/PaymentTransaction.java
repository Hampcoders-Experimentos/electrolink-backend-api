package com.hampcoders.electrolink.subscription.domain.model.entities;

import com.hampcoders.electrolink.shared.domain.model.entities.AuditableModel;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_payment_transactions")
@Getter
@NoArgsConstructor
public class PaymentTransaction extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long subscriptionId;

    @Column(nullable = false)
    private Long userId;

    @Embedded
    private Money amount;

    @Column(nullable = false)
    private String paymentGatewayReference;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    public PaymentTransaction(Long subscriptionId, Long userId, Money amount,
                              String paymentGatewayReference, String status) {
        this.subscriptionId = subscriptionId;
        this.userId = userId;
        this.amount = amount;
        this.paymentGatewayReference = paymentGatewayReference;
        this.status = status;
        this.paidAt = LocalDateTime.now();
    }
}
