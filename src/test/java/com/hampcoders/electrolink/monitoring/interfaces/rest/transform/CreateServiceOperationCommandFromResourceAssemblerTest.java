package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.monitoring.domain.model.commands.CreateServiceOperationCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.CreateServiceOperationResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateServiceOperationCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource, when assembling, then it maps ids and defaults to IN_PROGRESS")
  void handle_ShouldMapFieldsAndDefaultStatus_WhenResourceProvided() {
    // Arrange
    CreateServiceOperationResource resource = new CreateServiceOperationResource(1L, 2L);

    // Act
    CreateServiceOperationCommand command =
        CreateServiceOperationCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(new RequestId(1L), command.requestId());
    assertEquals(new TechnicianId(2L), command.technicianId());
    assertEquals(ServiceStatus.IN_PROGRESS, command.currentStatus());
    assertNotNull(command.startedAt());
    assertNull(command.completedAt());
  }

  @Test
  @DisplayName("Given different ids, when assembling, then it maps the new ids")
  void handle_ShouldMapNewIds_WhenDifferentResourceProvided() {
    // Arrange
    CreateServiceOperationResource resource = new CreateServiceOperationResource(9L, 8L);

    // Act
    CreateServiceOperationCommand command =
        CreateServiceOperationCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(new RequestId(9L), command.requestId());
    assertEquals(new TechnicianId(8L), command.technicianId());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreateServiceOperationCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
