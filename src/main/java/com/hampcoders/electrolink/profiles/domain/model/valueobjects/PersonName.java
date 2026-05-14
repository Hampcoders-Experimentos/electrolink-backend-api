package com.hampcoders.electrolink.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing a person's name, consisting of a first name and a last name.
 *
 * @param firstName the first name of the person, must not be null or blank
 * @param lastName the last name of the person, must not be null or blank
 */
@Embeddable
public record PersonName(String firstName, String lastName) {

  /**
   * Default constructor for JPA.
   */
  public PersonName() {
    this(null, null);
  }

  /**
   * Constructs a new PersonName with the given first name and last name.
   */
  public PersonName {
    if (firstName == null || firstName.isBlank()) {
      throw new IllegalArgumentException("First name cannot be null or blank");
    }
    if (lastName == null || lastName.isBlank()) {
      throw new IllegalArgumentException("Last name cannot be null or blank");
    }
  }
}
