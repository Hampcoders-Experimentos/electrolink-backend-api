package com.hampcoders.electrolink.iam.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.iam.domain.model.entities.Role;
import com.hampcoders.electrolink.iam.domain.model.valueobjects.Roles;
import com.hampcoders.electrolink.iam.interfaces.rest.resources.RoleResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoleResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a complete role, when assembling, then it maps id and string name")
  void handle_ShouldMapIdAndName_WhenRoleIsComplete() {
    // Arrange
    Role role = new Role(1L, Roles.ROLE_CLIENT);

    // Act
    RoleResource resource = RoleResourceFromEntityAssembler.toResourceFromEntity(role);

    // Assert
    assertEquals(1L, resource.id());
    assertEquals("ROLE_CLIENT", resource.name());
  }

  @Test
  @DisplayName("Given a role with a null name, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenNameIsNull() {
    // Arrange
    Role role = new Role();

    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> RoleResourceFromEntityAssembler.toResourceFromEntity(role));
  }

  @Test
  @DisplayName("Given an unsaved role, when assembling, then the resource id is null")
  void handle_ShouldMapNullId_WhenRoleNotPersisted() {
    // Arrange
    Role role = new Role(Roles.ROLE_TECHNICIAN);

    // Act
    RoleResource resource = RoleResourceFromEntityAssembler.toResourceFromEntity(role);

    // Assert
    assertNull(resource.id());
    assertEquals("ROLE_TECHNICIAN", resource.name());
  }
}
