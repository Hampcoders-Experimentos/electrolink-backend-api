package com.hampcoders.electrolink.profiles.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.profiles.domain.model.commands.UpdateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateProfileCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource and id, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    ProfileResource resource = new ProfileResource(
        1L, "John", "Doe", "john@mail.com", "Main St", Role.HOMEOWNER, "info", null);

    // Act
    UpdateProfileCommand command =
        UpdateProfileCommandFromResourceAssembler.toCommandFromResource(5L, resource);

    // Assert
    assertEquals(5L, command.profileId());
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
    ProfileResource resource = new ProfileResource(
        2L, "Jane", "Tech", "jane@mail.com", "Second St", Role.TECHNICIAN, "CERT-1", true);

    // Act
    UpdateProfileCommand command =
        UpdateProfileCommandFromResourceAssembler.toCommandFromResource(8L, resource);

    // Assert
    assertEquals(8L, command.profileId());
    assertEquals(Role.TECHNICIAN, command.role());
    assertEquals("CERT-1", command.additionalInfoOrCertification());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> UpdateProfileCommandFromResourceAssembler.toCommandFromResource(5L, null));
  }
}
