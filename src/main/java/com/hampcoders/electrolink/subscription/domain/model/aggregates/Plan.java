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

/**
 * Plan aggregate root representing a subscription plan in the system.
 */
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

  /**
   * Constructor to create a new Plan based on the provided command.
   *
   * @param command the command containing the details for creating the plan
   */
  public Plan(CreatePlanCommand command) {
    this.name = command.name();
    this.description = command.description();
    this.price = Money.usd(command.price());
    this.maxRequestsPerMonth = command.maxRequestsPerMonth();
    this.prioritySupport = command.prioritySupport();
    this.isActive = true;
  }

  /**
   * Checks if the user can make a request based on the current monthly requests
   * and the plan's limit.
   *
   * @param currentMonthlyRequests the number of requests the user has made in the current month
   * @return true if the user can make a request, false otherwise
   */
  public boolean canMakeRequest(int currentMonthlyRequests) {
    return currentMonthlyRequests < maxRequestsPerMonth;
  }

  /**
   * Deactivates the plan, making it unavailable for new subscriptions.
   * Existing subscribers will retain access until they cancel or switch plans.
   */
  public void deactivate() {
    this.isActive = false;
  }

  /**
   * Activates the plan, making it available for new subscriptions.
   * This can be used to re-enable a previously deactivated plan.
   */
  public void activate() {
    this.isActive = true;
  }

  /**
   * Checks if the plan is a premium plan.
   *
   * @return true if the plan is premium, false otherwise
   */
  public boolean isPremium() {
    return name == PlanType.PREMIUM;
  }

  /**
   * Checks if the plan is a basic plan.
   *
   * @return true if the plan is basic, false otherwise
   */
  public boolean isBasic() {
    return name == PlanType.BASIC;
  }

  /**
   * Factory method to create a basic plan with predefined settings.
   * This can be used for testing or as a default plan in the system.
   *
   * @return a new instance of the basic plan
   */
  public static Plan createBasicPlan() {
    return new Plan(new CreatePlanCommand(
        PlanType.BASIC, "Free plan with limited monthly requests", 0, 2, false
    ));
  }

  /**
   * Factory method to create a premium plan with predefined settings.
   * This can be used for testing or as a default plan in the system.
   *
   * @return a new instance of the premium plan
   */
  public static Plan createPremiumPlan() {
    return new Plan(new CreatePlanCommand(
        PlanType.PREMIUM, "Unlimited requests with priority support",
        29.99, Integer.MAX_VALUE, true
    ));
  }
}
