package com.hampcoders.electrolink.iam.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hampcoders.electrolink.iam.domain.model.aggregates.User;
import com.hampcoders.electrolink.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.hampcoders.electrolink.shared.test.fixtures.UserInputFixture;
import com.hampcoders.electrolink.shared.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthenticatedUserResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a persisted user and a token, when assembling, then it maps id, username and token")
  void handle_ShouldMapAllFields_WhenUserAndTokenProvided() {
    // Arrange
    User user = new User(UserInputFixture.username(), "hashed");
    ReflectionTestUtils.setField(user, "id", 1L);

    // Act
    AuthenticatedUserResource resource =
        AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(user, "jwt");

    // Assert
    assertEquals(1L, resource.id());
    assertEquals(UserInputFixture.username(), resource.username());
    assertEquals("jwt", resource.token());
  }

  @Test
  @DisplayName("Given a null token, when assembling, then the resource token is null")
  void handle_ShouldMapNullToken_WhenTokenIsNull() {
    // Arrange
    User user = new User(UserInputFixture.username(), "hashed");
    ReflectionTestUtils.setField(user, "id", 1L);

    // Act
    AuthenticatedUserResource resource =
        AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(user, null);

    // Assert
    assertNull(resource.token());
  }

  @Test
  @DisplayName("Given an unsaved user, when assembling, then the resource id is null")
  void handle_ShouldMapNullId_WhenUserNotPersisted() {
    // Arrange
    User user = new User(UserInputFixture.username(), "hashed");

    // Act
    AuthenticatedUserResource resource =
        AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(user, "jwt");

    // Assert
    assertNull(resource.id());
  }
}
