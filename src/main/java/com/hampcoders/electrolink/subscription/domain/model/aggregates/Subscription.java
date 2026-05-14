package com.hampcoders.electrolink.subscription.domain.model.aggregates;

import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
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

/**
 * Represents a user's subscription to a plan, including status,
 * start/end dates, and request tracking.
 */
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

  /**
   * Creates a new subscription based on the provided command and plan.
   *
   * @param command The command containing user ID and other
   *      necessary information to create the subscription.
   * @param plan The plan to which the user is subscribing,
   *      which determines the request limits and features.
   */
  public Subscription(CreateSubscriptionCommand command, Plan plan) {
    this.userId = command.userId();
    this.plan = plan;
    this.status = SubscriptionStatus.ACTIVE;
    this.startDate = LocalDateTime.now();
    this.monthlyRequestCount = 0;
    this.monthlyRequestResetDate = LocalDateTime.now().plusMonths(1);
    registerEvent(new SubscriptionActivatedEvent(command.userId(), plan.getId(), plan.getName()));
  }

  /**
   * Checks if the subscription can make a request based on the current plan's limits.
   *
   * @return true if the subscription can make a request,
   *      false if the monthly limit has been reached
   */
  public boolean canMakeRequest() {
    resetMonthlyCountIfNeeded();
    return plan.canMakeRequest(monthlyRequestCount);
  }

  /**
   * Records a request made by the user.
   * This method should be called whenever the user makes a request
   */
  public void recordRequest() {
    resetMonthlyCountIfNeeded();
    if (!canMakeRequest()) {
      throw new IllegalStateException("Monthly request limit reached for plan: " + plan.getName());
    }
    monthlyRequestCount++;
  }

  /**
   * Upgrades the subscription to a new plan.
   * This resets the monthly request count and
   * sets a new reset date for the monthly request count.
   *
   * @param newPlan The new plan to which the subscription will be upgraded,
   *      which may have different request limits and features
   */
  public void upgradeTo(Plan newPlan) {
    this.plan = newPlan;
    this.monthlyRequestCount = 0;
    this.monthlyRequestResetDate = LocalDateTime.now().plusMonths(1);
  }

  /**
   * Cancels the subscription, setting its status to CANCELLED and recording the end date.
   */
  public void cancel() {
    this.status = SubscriptionStatus.CANCELLED;
    this.endDate = LocalDateTime.now();
    registerEvent(new SubscriptionCancelledEvent(userId, plan.getId()));
  }

  /**
   * Resets the monthly request count if the current date is past the reset date.
   */
  private void resetMonthlyCountIfNeeded() {
    if (LocalDateTime.now().isAfter(monthlyRequestResetDate)) {
      monthlyRequestCount = 0;
      monthlyRequestResetDate = LocalDateTime.now().plusMonths(1);
    }
  }

  /**
   * Checks if the subscription is currently active.
   *
   * @return true if the subscription status is ACTIVE, false otherwise
   */
  public boolean isActive() {
    return status == SubscriptionStatus.ACTIVE;
  }

  /**
   * Checks if the subscription is currently canceled.
   *
   * @return true if the subscription status is CANCELLED, false otherwise
   */
  public boolean isPremium() {
    return plan.isPremium();
  }
}
