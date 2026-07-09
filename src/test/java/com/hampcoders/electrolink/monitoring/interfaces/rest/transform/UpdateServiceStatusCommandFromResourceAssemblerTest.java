package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.monitoring.domain.model.commands.UpdateServiceStatusCommand;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.UpdateServiceStatusResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateServiceStatusCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource, when assembling, then it maps id and status")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    UpdateServiceStatusResource resource = new UpdateServiceStatusResource(10L, "COMPLETED");

    // Act
    UpdateServiceStatusCommand command =
        UpdateServiceStatusCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(10L, command.serviceOperationId());
    assertEquals("COMPLETED", command.newStatus());
  }

  @Test
  @DisplayName("Given a different status, when assembling, then it maps the new status")
  void handle_ShouldMapNewStatus_WhenDifferentResourceProvided() {
    // Arrange
    UpdateServiceStatusResource resource = new UpdateServiceStatusResource(11L, "CANCELLED");

    // Act
    UpdateServiceStatusCommand command =
        UpdateServiceStatusCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(11L, command.serviceOperationId());
    assertEquals("CANCELLED", command.newStatus());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> UpdateServiceStatusCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
