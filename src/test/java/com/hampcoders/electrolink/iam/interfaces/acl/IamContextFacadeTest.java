package com.hampcoders.electrolink.iam.interfaces.acl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.iam.domain.model.aggregates.User;
import com.hampcoders.electrolink.iam.domain.model.commands.SignUpCommand;
import com.hampcoders.electrolink.iam.domain.model.entities.Role;
import com.hampcoders.electrolink.iam.domain.model.queries.GetUserByIdQuery;
import com.hampcoders.electrolink.iam.domain.model.queries.GetUserByUsernameQuery;
import com.hampcoders.electrolink.iam.domain.model.valueobjects.Roles;
import com.hampcoders.electrolink.iam.domain.services.UserCommandService;
import com.hampcoders.electrolink.iam.domain.services.UserQueryService;
import com.hampcoders.electrolink.shared.test.fixtures.UserInputFixture;
import com.hampcoders.electrolink.shared.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IamContextFacadeTest {

  @Mock
  private UserCommandService userCommandService;
  @Mock
  private UserQueryService userQueryService;

  @InjectMocks
  private IamContextFacade iamContextFacade;

  private static User userWithId(Long id) {
    User user = new User(UserInputFixture.username(), "hashed");
    ReflectionTestUtils.setField(user, "id", id);
    return user;
  }

  @Test
  @DisplayName("Given a created user, when creating a user, then it returns the new user id with the default role")
  void handle_ShouldReturnId_WhenUserCreated() {
    // Arrange
    when(userCommandService.handle(any(SignUpCommand.class)))
        .thenReturn(Optional.of(userWithId(5L)));
    ArgumentCaptor<SignUpCommand> captor = ArgumentCaptor.forClass(SignUpCommand.class);

    // Act
    Long id = iamContextFacade.createUser(UserInputFixture.username(), UserInputFixture.password());

    // Assert
    assertEquals(5L, id);
    org.mockito.Mockito.verify(userCommandService).handle(captor.capture());
    assertEquals(List.of(Role.getDefaultRole()), captor.getValue().roles());
  }

  @Test
  @DisplayName("Given creation returns empty, when creating a user, then it returns zero")
  void handle_ShouldReturnZero_WhenCreationEmpty() {
    // Arrange
    when(userCommandService.handle(any(SignUpCommand.class))).thenReturn(Optional.empty());

    // Act
    Long id = iamContextFacade.createUser(UserInputFixture.username(), UserInputFixture.password());

    // Assert
    assertEquals(0L, id);
  }

  @Test
  @DisplayName("Given explicit role names, when creating a user, then it maps them into the sign-up command")
  void handle_ShouldMapRoles_WhenRoleNamesProvided() {
    // Arrange
    when(userCommandService.handle(any(SignUpCommand.class)))
        .thenReturn(Optional.of(userWithId(7L)));
    ArgumentCaptor<SignUpCommand> captor = ArgumentCaptor.forClass(SignUpCommand.class);

    // Act
    Long id = iamContextFacade.createUser(
        UserInputFixture.username(), UserInputFixture.password(), List.of("ROLE_TECHNICIAN"));

    // Assert
    assertEquals(7L, id);
    org.mockito.Mockito.verify(userCommandService).handle(captor.capture());
    assertEquals(List.of(new Role(Roles.ROLE_TECHNICIAN)), captor.getValue().roles());
  }

  @Test
  @DisplayName("Given the username exists, when fetching the user id by username, then it returns the id")
  void handle_ShouldReturnId_WhenUsernameFound() {
    // Arrange
    when(userQueryService.handle(any(GetUserByUsernameQuery.class)))
        .thenReturn(Optional.of(userWithId(9L)));

    // Act
    Long id = iamContextFacade.fetchUserIdByUsername(UserInputFixture.username());

    // Assert
    assertEquals(9L, id);
  }

  @Test
  @DisplayName("Given the username is missing, when fetching the user id by username, then it returns zero")
  void handle_ShouldReturnZero_WhenUsernameMissing() {
    // Arrange
    when(userQueryService.handle(any(GetUserByUsernameQuery.class))).thenReturn(Optional.empty());

    // Act
    Long id = iamContextFacade.fetchUserIdByUsername(UserInputFixture.username());

    // Assert
    assertEquals(0L, id);
  }

  @Test
  @DisplayName("Given the id is missing, when fetching the username by id, then it returns an empty string")
  void handle_ShouldReturnEmptyString_WhenIdMissing() {
    // Arrange
    when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.empty());

    // Act
    String username = iamContextFacade.fetchUsernameByUserId(1L);

    // Assert
    assertEquals("", username);
  }
}
