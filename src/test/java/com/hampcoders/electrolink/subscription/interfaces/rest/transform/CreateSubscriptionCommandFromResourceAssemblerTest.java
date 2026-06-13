package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.CreateSubscriptionResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateSubscriptionCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource, when assembling, then it maps user and plan ids")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    CreateSubscriptionResource resource = new CreateSubscriptionResource(2L, 1L);

    // Act
    CreateSubscriptionCommand command =
        CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(2L, command.userId());
    assertEquals(1L, command.planId());
  }

  @Test
  @DisplayName("Given different ids, when assembling, then it maps the new ids")
  void handle_ShouldMapNewIds_WhenDifferentResourceProvided() {
    // Arrange
    CreateSubscriptionResource resource = new CreateSubscriptionResource(9L, 8L);

    // Act
    CreateSubscriptionCommand command =
        CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(9L, command.userId());
    assertEquals(8L, command.planId());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
