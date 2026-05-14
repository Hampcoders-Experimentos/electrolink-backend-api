package com.hampcoders.electrolink.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing a street address.
 *
 * @param street the street address, must not be null or blank
 */
@Embeddable
public record StreetAddress(String street) {

  /**
   * Default constructor for JPA.
   */
  public StreetAddress() {
    this(null);
  }

  /**
   * Constructs a StreetAddress with the given street.
   *
   * @param street the street address, must not be null or blank
   */
  public StreetAddress {
    if (street == null || street.isBlank()) {
      throw new IllegalArgumentException("Address cannot be null or blank");
    }
  }
}
