package com.hampcoders.electrolink.iam.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.iam.application.internal.outboundservices.hashing.HashingService;
import com.hampcoders.electrolink.iam.application.internal.outboundservices.tokens.TokenService;
import com.hampcoders.electrolink.iam.domain.model.aggregates.User;
import com.hampcoders.electrolink.iam.domain.model.commands.SignInCommand;
import com.hampcoders.electrolink.iam.domain.model.commands.SignUpCommand;
import com.hampcoders.electrolink.iam.domain.model.entities.Role;
import com.hampcoders.electrolink.iam.domain.model.valueobjects.Roles;
import com.hampcoders.electrolink.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.hampcoders.electrolink.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.hampcoders.electrolink.shared.test.fixtures.CommonCommandFixtures;
import com.hampcoders.electrolink.shared.test.fixtures.UserInputFixture;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private HashingService hashingService;
  @Mock
  private TokenService tokenService;
  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private UserCommandServiceImpl userCommandService;

  @Test
  @DisplayName("Given valid credentials, when handling SignInCommand, then it returns the user and a token")
  void handle_ShouldReturnUserAndToken_WhenCredentialsValid() {
    // Arrange
    SignInCommand command = CommonCommandFixtures.signInCommand();
    User user = mock(User.class);
    when(userRepository.findByUsername(UserInputFixture.username())).thenReturn(Optional.of(user));
    when(user.getPassword()).thenReturn("hashed");
    when(hashingService.matches(UserInputFixture.password(), "hashed")).thenReturn(true);
    when(user.getUsername()).thenReturn(UserInputFixture.username());
    when(tokenService.generateToken(UserInputFixture.username())).thenReturn("jwt");

    // Act
    Optional<ImmutablePair<User, String>> result = userCommandService.handle(command);

    // Assert
    assertTrue(result.isPresent());
    assertSame(user, result.get().getLeft());
    assertEquals("jwt", result.get().getRight());
  }

  @Test
  @DisplayName("Given an unknown username, when handling SignInCommand, then it throws User not found")
  void handle_ShouldThrowUserNotFound_WhenUserMissingOnSignIn() {
    // Arrange
    SignInCommand command = CommonCommandFixtures.signInCommand();
    when(userRepository.findByUsername(UserInputFixture.username())).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException ex = assertThrows(RuntimeException.class, () -> userCommandService.handle(command));
    assertEquals("User not found", ex.getMessage());
  }

  @Test
  @DisplayName("Given a wrong password, when handling SignInCommand, then it throws Invalid password")
  void handle_ShouldThrowInvalidPassword_WhenPasswordInvalidOnSignIn() {
    // Arrange
    SignInCommand command = CommonCommandFixtures.signInCommand();
    User user = mock(User.class);
    when(userRepository.findByUsername(UserInputFixture.username())).thenReturn(Optional.of(user));
    when(user.getPassword()).thenReturn("hashed");
    when(hashingService.matches(UserInputFixture.password(), "hashed")).thenReturn(false);

    // Act & Assert
    RuntimeException ex = assertThrows(RuntimeException.class, () -> userCommandService.handle(command));
    assertEquals("Invalid password", ex.getMessage());
  }

  @Test
  @DisplayName("Given a new username with existing roles, when handling SignUpCommand, then it persists and returns the user")
  void handle_ShouldCreateUser_WhenUsernameIsNew() {
    // Arrange
    Role role = new Role(Roles.ROLE_CLIENT);
    SignUpCommand command = CommonCommandFixtures.signUpCommand(List.of(role));
    User saved = mock(User.class);
    when(userRepository.existsByUsername(UserInputFixture.username())).thenReturn(false);
    when(roleRepository.findByName(Roles.ROLE_CLIENT)).thenReturn(Optional.of(role));
    when(hashingService.encode(UserInputFixture.password())).thenReturn("hashed");
    when(userRepository.findByUsername(UserInputFixture.username())).thenReturn(Optional.of(saved));

    // Act
    Optional<User> result = userCommandService.handle(command);

    // Assert
    assertTrue(result.isPresent());
    assertSame(saved, result.get());
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Given an existing username, when handling SignUpCommand, then it throws Username already exists")
  void handle_ShouldThrowUsernameExists_WhenUsernameAlreadyExists() {
    // Arrange
    SignUpCommand command = CommonCommandFixtures.signUpCommand(List.of(new Role(Roles.ROLE_CLIENT)));
    when(userRepository.existsByUsername(UserInputFixture.username())).thenReturn(true);

    // Act & Assert
    RuntimeException ex = assertThrows(RuntimeException.class, () -> userCommandService.handle(command));
    assertEquals("Username already exists", ex.getMessage());
  }

  @Test
  @DisplayName("Given a role that does not exist, when handling SignUpCommand, then it throws Role name not found")
  void handle_ShouldThrowRoleNotFound_WhenRoleMissingOnSignUp() {
    // Arrange
    SignUpCommand command = CommonCommandFixtures.signUpCommand(List.of(new Role(Roles.ROLE_CLIENT)));
    when(userRepository.existsByUsername(UserInputFixture.username())).thenReturn(false);
    when(roleRepository.findByName(Roles.ROLE_CLIENT)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException ex = assertThrows(RuntimeException.class, () -> userCommandService.handle(command));
    assertEquals("Role name not found", ex.getMessage());
  }
}
