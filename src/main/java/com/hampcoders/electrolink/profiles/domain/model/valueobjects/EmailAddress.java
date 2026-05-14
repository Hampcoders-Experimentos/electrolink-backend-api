package com.hampcoders.electrolink.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

/**
 * Value object representing an email address.
 * It encapsulates the email address and provides validation to ensure
 * that the email address is in a valid format.
 *
 * @param address The email address string.
 *     It must be a valid email format and cannot be null or blank.
 */
@Embeddable
public record EmailAddress(String address) {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$", Pattern.CASE_INSENSITIVE);

  /**
   * Constructor by default for JPA.
   */
  public EmailAddress() {
    this(null);
  }

  /**
   * Constructor that validates the email address format.
   */
  public EmailAddress {
    if (address == null || address.isBlank()) {
      throw new IllegalArgumentException("Email cannot be null or blank");
    }
    if (!EMAIL_PATTERN.matcher(address).matches()) {
      throw new IllegalArgumentException("Invalid email address format");
    }
  }
}
