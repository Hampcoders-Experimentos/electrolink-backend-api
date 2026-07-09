package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.assets.domain.model.commands.UpdateComponentStockCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.UpdateComponentStockResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateComponentStockCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource with ids, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    UpdateComponentStockResource resource = new UpdateComponentStockResource(20, 5);

    // Act
    UpdateComponentStockCommand command =
        UpdateComponentStockCommandFromResourceAssembler.toCommandFromResource(7L, 10L, resource);

    // Assert
    assertEquals(7L, command.technicianId());
    assertEquals(10L, command.componentId());
    assertEquals(20, command.newQuantity());
    assertEquals(5, command.newAlertThreshold());
  }

  @Test
  @DisplayName("Given different values, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentValuesProvided() {
    // Arrange
    UpdateComponentStockResource resource = new UpdateComponentStockResource(0, 1);

    // Act
    UpdateComponentStockCommand command =
        UpdateComponentStockCommandFromResourceAssembler.toCommandFromResource(3L, 99L, resource);

    // Assert
    assertEquals(3L, command.technicianId());
    assertEquals(99L, command.componentId());
    assertEquals(0, command.newQuantity());
    assertEquals(1, command.newAlertThreshold());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> UpdateComponentStockCommandFromResourceAssembler
            .toCommandFromResource(7L, 10L, null));
  }
}
