package com.hampcoders.electrolink.iam.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.iam.domain.model.commands.SignInCommand;
import com.hampcoders.electrolink.iam.interfaces.rest.resources.SignInResource;
import com.hampcoders.electrolink.shared.test.fixtures.UserInputFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignInCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a complete resource, when assembling, then it maps username and password")
  void handle_ShouldMapFields_WhenResourceIsComplete() {
    // Arrange
    SignInResource resource =
        new SignInResource(UserInputFixture.username(), UserInputFixture.password());

    // Act
    SignInCommand command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(UserInputFixture.username(), command.username());
    assertEquals(UserInputFixture.password(), command.password());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> SignInCommandFromResourceAssembler.toCommandFromResource(null));
  }

  @Test
  @DisplayName("Given null fields, when assembling, then the command keeps the null values")
  void handle_ShouldMapNulls_WhenFieldsAreNull() {
    // Arrange
    SignInResource resource = new SignInResource(null, null);

    // Act
    SignInCommand command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertNull(command.username());
    assertNull(command.password());
  }
}
