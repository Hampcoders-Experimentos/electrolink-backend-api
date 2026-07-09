package com.hampcoders.electrolink.iam.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.iam.domain.model.commands.SeedRolesCommand;
import com.hampcoders.electrolink.iam.domain.model.entities.Role;
import com.hampcoders.electrolink.iam.domain.model.valueobjects.Roles;
import com.hampcoders.electrolink.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleCommandServiceImplTest {

  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private RoleCommandServiceImpl roleCommandService;

  @Test
  @DisplayName("Given no roles exist, when handling SeedRolesCommand, then it saves every role")
  void handle_ShouldSaveAllRoles_WhenNoneExist() {
    // Arrange
    when(roleRepository.existsByName(any(Roles.class))).thenReturn(false);

    // Act
    roleCommandService.handle(new SeedRolesCommand());

    // Assert
    verify(roleRepository, times(Roles.values().length)).save(any(Role.class));
  }

  @Test
  @DisplayName("Given all roles already exist, when handling SeedRolesCommand, then it saves none")
  void handle_ShouldSkipSaving_WhenAllRolesExist() {
    // Arrange
    when(roleRepository.existsByName(any(Roles.class))).thenReturn(true);

    // Act
    roleCommandService.handle(new SeedRolesCommand());

    // Assert
    verify(roleRepository, never()).save(any(Role.class));
  }

  @Test
  @DisplayName("Given the repository fails to save, when handling SeedRolesCommand, then it propagates the exception")
  void handle_ShouldPropagateException_WhenSaveFails() {
    // Arrange
    when(roleRepository.existsByName(any(Roles.class))).thenReturn(false);
    when(roleRepository.save(any(Role.class))).thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> roleCommandService.handle(new SeedRolesCommand()));
  }
}
