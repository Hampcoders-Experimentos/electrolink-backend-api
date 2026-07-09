package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.subscription.domain.model.commands.CreatePlanCommand;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.CreatePlanResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreatePlanCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a basic plan resource, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    CreatePlanResource resource = new CreatePlanResource(PlanType.BASIC, "desc", 9.99, 10, false);

    // Act
    CreatePlanCommand command =
        CreatePlanCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(PlanType.BASIC, command.name());
    assertEquals("desc", command.description());
    assertEquals(9.99, command.price());
    assertEquals(10, command.maxRequestsPerMonth());
    assertEquals(false, command.prioritySupport());
  }

  @Test
  @DisplayName("Given a premium plan resource, when assembling, then it maps the premium values")
  void handle_ShouldMapPremiumValues_WhenResourceIsPremium() {
    // Arrange
    CreatePlanResource resource = new CreatePlanResource(PlanType.PREMIUM, "premium", 19.99, 100,
        true);

    // Act
    CreatePlanCommand command =
        CreatePlanCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(PlanType.PREMIUM, command.name());
    assertEquals(100, command.maxRequestsPerMonth());
    assertEquals(true, command.prioritySupport());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreatePlanCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
