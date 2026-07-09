package com.hampcoders.electrolink.shared.test.fixtures;

import com.hampcoders.electrolink.iam.domain.model.commands.SignInCommand;
import com.hampcoders.electrolink.iam.domain.model.commands.SignUpCommand;
import com.hampcoders.electrolink.iam.domain.model.entities.Role;
import java.util.List;

/**
 * Factory methods for commonly needed command instances in tests.
 *
 * <p>Builds the canonical IAM authentication commands from the shared
 * {@link UserInputFixture} values so input data is not duplicated per test.</p>
 */
public final class CommonCommandFixtures {

  private CommonCommandFixtures() {
  }

  /**
   * @return A {@link SignInCommand} using the default user credentials.
   */
  public static SignInCommand signInCommand() {
    return new SignInCommand(UserInputFixture.username(), UserInputFixture.password());
  }

  /**
   * @return A {@link SignUpCommand} using the default credentials and no roles.
   */
  public static SignUpCommand signUpCommand() {
    return signUpCommand(List.of());
  }

  /**
   * @param roles The roles to assign to the new user.
   * @return A {@link SignUpCommand} using the default credentials.
   */
  public static SignUpCommand signUpCommand(List<Role> roles) {
    return new SignUpCommand(UserInputFixture.username(), UserInputFixture.password(), roles);
  }
}
