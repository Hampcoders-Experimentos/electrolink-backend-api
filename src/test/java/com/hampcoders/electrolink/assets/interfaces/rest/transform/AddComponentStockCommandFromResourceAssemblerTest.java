package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.assets.domain.model.commands.AddComponentStockCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.AddComponentStockResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddComponentStockCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource and technician id, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    AddComponentStockResource resource = new AddComponentStockResource(10L, 5, 2);

    // Act
    AddComponentStockCommand command =
        AddComponentStockCommandFromResourceAssembler.toCommandFromResource(7L, resource);

    // Assert
    assertEquals(7L, command.technicianId());
    assertEquals(10L, command.componentId());
    assertEquals(5, command.quantity());
    assertEquals(2, command.alertThreshold());
  }

  @Test
  @DisplayName("Given different values, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentValuesProvided() {
    // Arrange
    AddComponentStockResource resource = new AddComponentStockResource(99L, 1, 0);

    // Act
    AddComponentStockCommand command =
        AddComponentStockCommandFromResourceAssembler.toCommandFromResource(3L, resource);

    // Assert
    assertEquals(3L, command.technicianId());
    assertEquals(99L, command.componentId());
    assertEquals(1, command.quantity());
    assertEquals(0, command.alertThreshold());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> AddComponentStockCommandFromResourceAssembler.toCommandFromResource(7L, null));
  }
}
