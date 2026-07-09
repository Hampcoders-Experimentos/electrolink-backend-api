package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.assets.domain.model.commands.CreateComponentTypeCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreateComponentTypeResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateComponentTypeCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource, when assembling, then it maps name and description")
  void handle_ShouldMapFields_WhenResourceProvided() {
    // Arrange
    CreateComponentTypeResource resource = new CreateComponentTypeResource("Capacitors", "desc");

    // Act
    CreateComponentTypeCommand command =
        CreateComponentTypeCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals("Capacitors", command.name());
    assertEquals("desc", command.description());
  }

  @Test
  @DisplayName("Given another resource, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentResourceProvided() {
    // Arrange
    CreateComponentTypeResource resource = new CreateComponentTypeResource("Resistors", "other");

    // Act
    CreateComponentTypeCommand command =
        CreateComponentTypeCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals("Resistors", command.name());
    assertEquals("other", command.description());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreateComponentTypeCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
