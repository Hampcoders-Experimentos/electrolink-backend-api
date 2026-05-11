package com.hampcoders.electrolink.subscription.domain.model.aggregates;

import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_plans")
@Getter
@NoArgsConstructor
public class Plan extends AuditableAbstractAggregateRoot<Plan> {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PlanType name;

    @Column(nullable = false)
    private String description;

    @Embedded
    private Money price;

    @Column(nullable = false)
    private int maxRequestsPerMonth;

    @Column(nullable = false)
    private boolean prioritySupport;

    @Column(nullable = false)
    private boolean isActive;

    public Plan(PlanType name, String description, Money price,
                int maxRequestsPerMonth, boolean prioritySupport) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.maxRequestsPerMonth = maxRequestsPerMonth;
        this.prioritySupport = prioritySupport;
        this.isActive = true;
    }

    public boolean canMakeRequest(int currentMonthlyRequests) {
        return currentMonthlyRequests < maxRequestsPerMonth;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean isPremium() {
        return name == PlanType.PREMIUM;
    }

    public boolean isBasic() {
        return name == PlanType.BASIC;
    }

    public static Plan createBasicPlan() {
        return new Plan(
            PlanType.BASIC,
            "Free plan with limited monthly requests",
            Money.usd(0),
            2,
            false
        );
    }

    public static Plan createPremiumPlan() {
        return new Plan(
            PlanType.PREMIUM,
            "Unlimited requests with priority support",
            Money.usd(29.99),
            Integer.MAX_VALUE,
            true
        );
    }
}
