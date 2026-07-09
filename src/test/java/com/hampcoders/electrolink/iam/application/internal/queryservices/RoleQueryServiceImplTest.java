package com.hampcoders.electrolink.iam.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.iam.domain.model.entities.Role;
import com.hampcoders.electrolink.iam.domain.model.queries.GetAllRolesQuery;
import com.hampcoders.electrolink.iam.domain.model.queries.GetRoleByNameQuery;
import com.hampcoders.electrolink.iam.domain.model.valueobjects.Roles;
import com.hampcoders.electrolink.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleQueryServiceImplTest {

  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private RoleQueryServiceImpl roleQueryService;

  @Test
  @DisplayName("Given roles exist, when handling GetAllRolesQuery, then it returns all roles")
  void handle_ShouldReturnAllRoles_WhenRolesExist() {
    // Arrange
    List<Role> roles = List.of(new Role(Roles.ROLE_CLIENT), new Role(Roles.ROLE_TECHNICIAN));
    when(roleRepository.findAll()).thenReturn(roles);

    // Act
    List<Role> result = roleQueryService.handle(new GetAllRolesQuery());

    // Assert
    assertEquals(roles, result);
  }

  @Test
  @DisplayName("Given the role name exists, when handling GetRoleByNameQuery, then it returns the role")
  void handle_ShouldReturnRole_WhenNameExists() {
    // Arrange
    Role role = new Role(Roles.ROLE_CLIENT);
    when(roleRepository.findByName(Roles.ROLE_CLIENT)).thenReturn(Optional.of(role));

    // Act
    Optional<Role> result = roleQueryService.handle(new GetRoleByNameQuery(Roles.ROLE_CLIENT));

    // Assert
    assertTrue(result.isPresent());
    assertSame(role, result.get());
  }

  @Test
  @DisplayName("Given the role name does not exist, when handling GetRoleByNameQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenNameMissing() {
    // Arrange
    when(roleRepository.findByName(Roles.ROLE_HOMEOWNER)).thenReturn(Optional.empty());

    // Act
    Optional<Role> result = roleQueryService.handle(new GetRoleByNameQuery(Roles.ROLE_HOMEOWNER));

    // Assert
    assertTrue(result.isEmpty());
  }
}
