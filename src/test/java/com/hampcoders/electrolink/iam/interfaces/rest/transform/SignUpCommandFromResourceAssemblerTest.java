package com.hampcoders.electrolink.iam.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.iam.domain.model.commands.SignUpCommand;
import com.hampcoders.electrolink.iam.domain.model.entities.Role;
import com.hampcoders.electrolink.iam.domain.model.valueobjects.Roles;
import com.hampcoders.electrolink.iam.interfaces.rest.resources.SignUpResource;
import com.hampcoders.electrolink.shared.test.fixtures.UserInputFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignUpCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given explicit roles, when assembling, then it maps the requested roles")
  void handle_ShouldMapRequestedRoles_WhenRolesProvided() {
    // Arrange
    SignUpResource resource = new SignUpResource(
        UserInputFixture.username(), UserInputFixture.password(), List.of("ROLE_TECHNICIAN"));

    // Act
    SignUpCommand command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(UserInputFixture.username(), command.username());
    assertEquals(List.of(new Role(Roles.ROLE_TECHNICIAN)), command.roles());
  }

  @Test
  @DisplayName("Given null roles, when assembling, then it falls back to the default role")
  void handle_ShouldUseDefaultRole_WhenRolesAreNull() {
    // Arrange
    SignUpResource resource =
        new SignUpResource(UserInputFixture.username(), UserInputFixture.password(), null);

    // Act
    SignUpCommand command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(List.of(Role.getDefaultRole()), command.roles());
  }

  @Test
  @DisplayName("Given an unknown role name, when assembling, then it throws IllegalArgumentException")
  void handle_ShouldThrowIllegalArgument_WhenRoleNameInvalid() {
    // Arrange
    SignUpResource resource = new SignUpResource(
        UserInputFixture.username(), UserInputFixture.password(), List.of("ROLE_UNKNOWN"));

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> SignUpCommandFromResourceAssembler.toCommandFromResource(resource));
  }
}
