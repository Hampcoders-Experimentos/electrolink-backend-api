package com.hampcoders.electrolink.monitoring.application.internal.eventhandlers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.monitoring.domain.model.events.ServiceCompletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MutualEvaluationTriggerHandlerTest {

  private final MutualEvaluationTriggerHandler handler = new MutualEvaluationTriggerHandler();

  @Test
  @DisplayName("Given a valid event, when handling service completed, then it completes without error")
  void handle_ShouldComplete_WhenEventReceived() {
    // Arrange
    ServiceCompletedEvent event = new ServiceCompletedEvent(1L, 2L, 3L);

    // Act & Assert
    assertDoesNotThrow(() -> handler.onServiceCompleted(event));
  }

  @Test
  @DisplayName("Given an event with null ids, when handling service completed, then it completes without error")
  void handle_ShouldComplete_WhenIdsAreNull() {
    // Arrange
    ServiceCompletedEvent event = new ServiceCompletedEvent(null, null, null);

    // Act & Assert
    assertDoesNotThrow(() -> handler.onServiceCompleted(event));
  }

  @Test
  @DisplayName("Given a null event, when handling service completed, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenEventIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> handler.onServiceCompleted(null));
  }
}
