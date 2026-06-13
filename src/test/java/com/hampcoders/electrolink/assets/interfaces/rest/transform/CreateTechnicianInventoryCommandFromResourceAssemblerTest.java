package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.assets.domain.model.commands.CreateTechnicianInventoryCommand;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreateTechnicianInventoryResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateTechnicianInventoryCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource, when assembling, then it maps the technician id")
  void handle_ShouldMapTechnicianId_WhenResourceProvided() {
    // Arrange
    CreateTechnicianInventoryResource resource = new CreateTechnicianInventoryResource(7L);

    // Act
    CreateTechnicianInventoryCommand command =
        CreateTechnicianInventoryCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(new TechnicianId(7L), command.technicianId());
  }

  @Test
  @DisplayName("Given a different resource, when assembling, then it maps the new technician id")
  void handle_ShouldMapNewTechnicianId_WhenDifferentResourceProvided() {
    // Arrange
    CreateTechnicianInventoryResource resource = new CreateTechnicianInventoryResource(42L);

    // Act
    CreateTechnicianInventoryCommand command =
        CreateTechnicianInventoryCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(new TechnicianId(42L), command.technicianId());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreateTechnicianInventoryCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
