package com.hampcoders.electrolink.subscription.domain.model.aggregates;

import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.hampcoders.electrolink.subscription.domain.model.events.SubscriptionActivatedEvent;
import com.hampcoders.electrolink.subscription.domain.model.events.SubscriptionCancelledEvent;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_subscriptions")
@Getter
@NoArgsConstructor
public class Subscription extends AuditableAbstractAggregateRoot<Subscription> {

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column(nullable = false)
    private int monthlyRequestCount;

    @Column(nullable = false)
    private LocalDateTime monthlyRequestResetDate;

    public Subscription(Long userId, Plan plan) {
        this.userId = userId;
        this.plan = plan;
        this.status = SubscriptionStatus.ACTIVE;
        this.startDate = LocalDateTime.now();
        this.monthlyRequestCount = 0;
        this.monthlyRequestResetDate = LocalDateTime.now().plusMonths(1);
        registerEvent(new SubscriptionActivatedEvent(userId, plan.getId(), plan.getName()));
    }

    public boolean canMakeRequest() {
        resetMonthlyCountIfNeeded();
        return plan.canMakeRequest(monthlyRequestCount);
    }

    public void recordRequest() {
        resetMonthlyCountIfNeeded();
        if (!canMakeRequest()) {
            throw new IllegalStateException("Monthly request limit reached for plan: " + plan.getName());
        }
        monthlyRequestCount++;
    }

    public void upgradeTo(Plan newPlan) {
        this.plan = newPlan;
        this.monthlyRequestCount = 0;
        this.monthlyRequestResetDate = LocalDateTime.now().plusMonths(1);
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.endDate = LocalDateTime.now();
        registerEvent(new SubscriptionCancelledEvent(userId, plan.getId()));
    }

    private void resetMonthlyCountIfNeeded() {
        if (LocalDateTime.now().isAfter(monthlyRequestResetDate)) {
            monthlyRequestCount = 0;
            monthlyRequestResetDate = LocalDateTime.now().plusMonths(1);
        }
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    public boolean isPremium() {
        return plan.isPremium();
    }
}
