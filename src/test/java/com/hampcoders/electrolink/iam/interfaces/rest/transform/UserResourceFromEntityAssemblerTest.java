package com.hampcoders.electrolink.iam.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hampcoders.electrolink.iam.domain.model.aggregates.User;
import com.hampcoders.electrolink.iam.domain.model.entities.Role;
import com.hampcoders.electrolink.iam.domain.model.valueobjects.Roles;
import com.hampcoders.electrolink.iam.interfaces.rest.resources.UserResource;
import com.hampcoders.electrolink.shared.test.fixtures.UserInputFixture;
import com.hampcoders.electrolink.shared.test.util.ReflectionTestUtils;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a user with roles, when assembling, then it maps id, username and role names")
  void handle_ShouldMapAllFields_WhenUserHasRoles() {
    // Arrange
    User user = new User(
        UserInputFixture.username(), "hashed", List.of(new Role(Roles.ROLE_CLIENT)));
    ReflectionTestUtils.setField(user, "id", 1L);

    // Act
    UserResource resource = UserResourceFromEntityAssembler.toResourceFromEntity(user);

    // Assert
    assertEquals(1L, resource.id());
    assertEquals(UserInputFixture.username(), resource.username());
    assertEquals(List.of("ROLE_CLIENT"), resource.roles());
  }

  @Test
  @DisplayName("Given a user without roles, when assembling, then the role list is empty")
  void handle_ShouldMapEmptyRoles_WhenUserHasNoRoles() {
    // Arrange
    User user = new User(UserInputFixture.username(), "hashed");
    ReflectionTestUtils.setField(user, "id", 2L);

    // Act
    UserResource resource = UserResourceFromEntityAssembler.toResourceFromEntity(user);

    // Assert
    assertTrue(resource.roles().isEmpty());
  }

  @Test
  @DisplayName("Given a null user, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenUserIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> UserResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
