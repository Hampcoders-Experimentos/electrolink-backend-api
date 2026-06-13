package com.hampcoders.electrolink.iam.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.iam.domain.model.aggregates.User;
import com.hampcoders.electrolink.iam.domain.model.queries.GetAllUsersQuery;
import com.hampcoders.electrolink.iam.domain.model.queries.GetUserByIdQuery;
import com.hampcoders.electrolink.iam.domain.model.queries.GetUserByUsernameQuery;
import com.hampcoders.electrolink.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.hampcoders.electrolink.shared.test.fixtures.UserInputFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserQueryServiceImpl userQueryService;

  @Test
  @DisplayName("Given users exist, when handling GetAllUsersQuery, then it returns all users")
  void handle_ShouldReturnAllUsers_WhenUsersExist() {
    // Arrange
    List<User> users = List.of(mock(User.class), mock(User.class));
    when(userRepository.findAll()).thenReturn(users);

    // Act
    List<User> result = userQueryService.handle(new GetAllUsersQuery());

    // Assert
    assertEquals(users, result);
  }

  @Test
  @DisplayName("Given the id does not exist, when handling GetUserByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenIdMissing() {
    // Arrange
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    // Act
    Optional<User> result = userQueryService.handle(new GetUserByIdQuery(99L));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given the username exists, when handling GetUserByUsernameQuery, then it returns the user")
  void handle_ShouldReturnUser_WhenUsernameExists() {
    // Arrange
    User user = mock(User.class);
    when(userRepository.findByUsername(UserInputFixture.username())).thenReturn(Optional.of(user));

    // Act
    Optional<User> result =
        userQueryService.handle(new GetUserByUsernameQuery(UserInputFixture.username()));

    // Assert
    assertTrue(result.isPresent());
    assertSame(user, result.get());
  }
}
