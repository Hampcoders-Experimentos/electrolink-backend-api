package com.hampcoders.electrolink.iam.application.internal.commandservices;

import com.hampcoders.electrolink.iam.domain.model.commands.SeedRolesCommand;
import com.hampcoders.electrolink.iam.domain.model.entities.Role;
import com.hampcoders.electrolink.iam.domain.model.valueobjects.Roles;
import com.hampcoders.electrolink.iam.domain.services.RoleCommandService;
import com.hampcoders.electrolink.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import java.util.Arrays;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link RoleCommandService} to handle {@link SeedRolesCommand}.
 */
@Service
public class RoleCommandServiceImpl implements RoleCommandService {

  private final RoleRepository roleRepository;

  /**
   * Constructor for RoleCommandServiceImpl.
   *
   * @param roleRepository the repository to manage Role entities
   */
  public RoleCommandServiceImpl(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  /**
   * This method will handle the {@link SeedRolesCommand} and will create the roles if not exists.
   *
   * @param command {@link SeedRolesCommand}
   * @see SeedRolesCommand
   */
  @Override
  public void handle(SeedRolesCommand command) {
    Arrays.stream(Roles.values())
        .forEach(role -> {
          if (!roleRepository.existsByName(role)) { // SPACE ADDED HERE
            roleRepository.save(new Role(Roles.valueOf(role.name())));
          }
        });
  }
}