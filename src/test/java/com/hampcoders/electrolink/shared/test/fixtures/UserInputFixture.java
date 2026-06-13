package com.hampcoders.electrolink.shared.test.fixtures;

/**
 * Reusable raw user-input values for tests.
 *
 * <p>Centralizes the default username/password used across command and service
 * tests so they stay consistent and are defined in a single place.</p>
 */
public final class UserInputFixture {

  public static final String DEFAULT_USERNAME = "test.user";
  public static final String DEFAULT_PASSWORD = "Passw0rd!";

  private UserInputFixture() {
  }

  /**
   * @return A valid default username.
   */
  public static String username() {
    return DEFAULT_USERNAME;
  }

  /**
   * @return A valid default raw password.
   */
  public static String password() {
    return DEFAULT_PASSWORD;
  }
}
