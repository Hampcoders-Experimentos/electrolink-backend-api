package com.hampcoders.electrolink.subscription.application.internal.eventhandlers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.subscription.domain.model.events.SubscriptionActivatedEvent;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubscriptionEventPublisherTest {

  private final SubscriptionEventPublisher publisher = new SubscriptionEventPublisher();

  @Test
  @DisplayName("Given a valid event, when handling subscription activated, then it completes without error")
  void handle_ShouldComplete_WhenEventReceived() {
    // Arrange
    SubscriptionActivatedEvent event = new SubscriptionActivatedEvent(2L, 1L, PlanType.PREMIUM);

    // Act & Assert
    assertDoesNotThrow(() -> publisher.onSubscriptionActivated(event));
  }

  @Test
  @DisplayName("Given an event with a null plan name, when handling subscription activated, then it completes without error")
  void handle_ShouldComplete_WhenPlanNameIsNull() {
    // Arrange
    SubscriptionActivatedEvent event = new SubscriptionActivatedEvent(2L, 1L, null);

    // Act & Assert
    assertDoesNotThrow(() -> publisher.onSubscriptionActivated(event));
  }

  @Test
  @DisplayName("Given a null event, when handling subscription activated, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenEventIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> publisher.onSubscriptionActivated(null));
  }
}
