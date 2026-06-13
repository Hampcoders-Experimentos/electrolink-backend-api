package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hampcoders.electrolink.assets.domain.model.commands.CreateComponentCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreateComponentResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateComponentCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given an active resource, when assembling, then it maps fields and generates an id")
  void handle_ShouldMapFieldsAndGenerateId_WhenResourceIsActive() {
    // Arrange
    CreateComponentResource resource = new CreateComponentResource("Resistor", "desc", 5L, true);

    // Act
    CreateComponentCommand command =
        CreateComponentCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertNotNull(command.componentId());
    assertEquals("Resistor", command.name());
    assertEquals("desc", command.description());
    assertEquals(5L, command.componentTypeId());
    assertTrue(command.isActive());
  }

  @Test
  @DisplayName("Given a null isActive, when assembling, then it defaults to active")
  void handle_ShouldDefaultActive_WhenIsActiveIsNull() {
    // Arrange
    CreateComponentResource resource = new CreateComponentResource("Resistor", "desc", 5L, null);

    // Act
    CreateComponentCommand command =
        CreateComponentCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertTrue(command.isActive());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreateComponentCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
