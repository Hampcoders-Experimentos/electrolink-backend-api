package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hampcoders.electrolink.assets.domain.model.commands.UpdateComponentCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.UpdateComponentResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateComponentCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource and id, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    UpdateComponentResource resource = new UpdateComponentResource("Resistor", "desc", 5L, true);

    // Act
    UpdateComponentCommand command =
        UpdateComponentCommandFromResourceAssembler.toCommandFromResource(7L, resource);

    // Assert
    assertEquals(7L, command.componentId());
    assertEquals("Resistor", command.name());
    assertEquals("desc", command.description());
    assertEquals(5L, command.componentTypeId());
    assertTrue(command.isActive());
  }

  @Test
  @DisplayName("Given a different id, when assembling, then it maps the new id")
  void handle_ShouldMapNewId_WhenDifferentIdProvided() {
    // Arrange
    UpdateComponentResource resource = new UpdateComponentResource("Capacitor", "other", 6L, false);

    // Act
    UpdateComponentCommand command =
        UpdateComponentCommandFromResourceAssembler.toCommandFromResource(99L, resource);

    // Assert
    assertEquals(99L, command.componentId());
    assertEquals("Capacitor", command.name());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> UpdateComponentCommandFromResourceAssembler.toCommandFromResource(7L, null));
  }
}
