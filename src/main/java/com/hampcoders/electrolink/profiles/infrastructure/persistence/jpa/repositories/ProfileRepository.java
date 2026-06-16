package com.hampcoders.electrolink.profiles.infrastructure.persistence.jpa.repositories;

import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Profile entities in the database.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  /**
   * Finds the profiles matching the person's first name and last name.
   *
   * <p>First name + last name is not unique, so this returns a list rather than an
   * {@link Optional}. Returning an {@code Optional} would throw
   * {@code NonUniqueResultException} as soon as more than one profile shares the name.</p>
   *
   * @param firstName the first name of the person
   * @param lastName the last name of the person
   * @return the list of matching profiles (empty if none)
   */
  List<Profile> findByPersonName_FirstNameAndPersonName_LastName(String firstName,
                                                                 String lastName);

  /**
   * Finds a Profile by the email address.
   *
   * @param email the email address of the profile
   * @return an Optional containing the profile if found,
   *     or empty if no profile with the specified email exists
   */
  Optional<Profile> findByEmail_Address(String email);

  /**
   * Finds a list of Profiles by their role.
   *
   * @param role the role of the profiles to find
   * @return a list of profiles with the specified role
   */
  List<Profile> findByRole(Role role);

  /**
   * Checks if a Profile exists with the given email address.
   *
   * @param email the email address to check
   * @return true if a profile with the specified email exists, false otherwise
   */
  boolean existsByEmail_Address(String email);

  /**
   * Checks if a Profile exists with the given email address, excluding a specific profile ID.
   *
   * @param email the email address to check
   * @param id the ID of the profile to exclude from the check
   * @return true if a profile with the specified email exists
   *     and has a different ID, false otherwise
   */
  boolean existsByEmail_AddressAndIdIsNot(String email, Long id);
}
