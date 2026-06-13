package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.SubscriptionStatus;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.SubscriptionResource;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubscriptionResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given an active subscription, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenSubscriptionIsActive() {
    // Arrange
    LocalDateTime startDate = LocalDateTime.now();
    Plan plan = mock(Plan.class);
    when(plan.getId()).thenReturn(1L);
    when(plan.getName()).thenReturn(PlanType.PREMIUM);
    Subscription subscription = mock(Subscription.class);
    when(subscription.getId()).thenReturn(5L);
    when(subscription.getUserId()).thenReturn(2L);
    when(subscription.getPlan()).thenReturn(plan);
    when(subscription.getStatus()).thenReturn(SubscriptionStatus.ACTIVE);
    when(subscription.getStartDate()).thenReturn(startDate);
    when(subscription.getMonthlyRequestCount()).thenReturn(3);
    when(subscription.canMakeRequest()).thenReturn(true);

    // Act
    SubscriptionResource resource =
        SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription);

    // Assert
    assertEquals(5L, resource.id());
    assertEquals(2L, resource.userId());
    assertEquals(1L, resource.planId());
    assertEquals("PREMIUM", resource.planName());
    assertEquals("ACTIVE", resource.status());
    assertEquals(startDate, resource.startDate());
    assertEquals(3, resource.monthlyRequestCount());
    assertTrue(resource.canMakeRequest());
  }

  @Test
  @DisplayName("Given a cancelled subscription, when assembling, then it maps the cancelled status")
  void handle_ShouldMapCancelledStatus_WhenSubscriptionIsCancelled() {
    // Arrange
    Plan plan = mock(Plan.class);
    when(plan.getId()).thenReturn(1L);
    when(plan.getName()).thenReturn(PlanType.BASIC);
    Subscription subscription = mock(Subscription.class);
    when(subscription.getId()).thenReturn(6L);
    when(subscription.getUserId()).thenReturn(3L);
    when(subscription.getPlan()).thenReturn(plan);
    when(subscription.getStatus()).thenReturn(SubscriptionStatus.CANCELLED);
    when(subscription.getStartDate()).thenReturn(LocalDateTime.now());
    when(subscription.getMonthlyRequestCount()).thenReturn(0);
    when(subscription.canMakeRequest()).thenReturn(false);

    // Act
    SubscriptionResource resource =
        SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription);

    // Assert
    assertEquals("CANCELLED", resource.status());
    assertFalse(resource.canMakeRequest());
  }

  @Test
  @DisplayName("Given a null subscription, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenSubscriptionIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> SubscriptionResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
