package com.hampcoders.electrolink.profiles.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.profiles.domain.model.commands.CreateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.CreateProfileResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateProfileCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a homeowner resource, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    CreateProfileResource resource = new CreateProfileResource(
        "John", "Doe", "john@mail.com", "Main St", Role.HOMEOWNER, "info");

    // Act
    CreateProfileCommand command =
        CreateProfileCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals("John", command.firstName());
    assertEquals("Doe", command.lastName());
    assertEquals("john@mail.com", command.email());
    assertEquals("Main St", command.street());
    assertEquals(Role.HOMEOWNER, command.role());
    assertEquals("info", command.additionalInfoOrCertification());
  }

  @Test
  @DisplayName("Given a technician resource, when assembling, then it maps the technician role")
  void handle_ShouldMapTechnicianRole_WhenResourceIsTechnician() {
    // Arrange
    CreateProfileResource resource = new CreateProfileResource(
        "Jane", "Tech", "jane@mail.com", "Second St", Role.TECHNICIAN, "CERT-1");

    // Act
    CreateProfileCommand command =
        CreateProfileCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(Role.TECHNICIAN, command.role());
    assertEquals("CERT-1", command.additionalInfoOrCertification());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreateProfileCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
