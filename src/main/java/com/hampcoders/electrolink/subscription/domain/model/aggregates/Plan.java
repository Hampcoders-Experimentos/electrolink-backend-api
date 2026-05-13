package com.hampcoders.electrolink.subscription.domain.model.aggregates;

import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreatePlanCommand;
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

    public Plan(CreatePlanCommand command) {
        this.name = command.name();
        this.description = command.description();
        this.price = Money.usd(command.price());
        this.maxRequestsPerMonth = command.maxRequestsPerMonth();
        this.prioritySupport = command.prioritySupport();
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
        return new Plan(new CreatePlanCommand(
            PlanType.BASIC, "Free plan with limited monthly requests", 0, 2, false
        ));
    }

    public static Plan createPremiumPlan() {
        return new Plan(new CreatePlanCommand(
            PlanType.PREMIUM, "Unlimited requests with priority support",
            29.99, Integer.MAX_VALUE, true
        ));
    }
}
