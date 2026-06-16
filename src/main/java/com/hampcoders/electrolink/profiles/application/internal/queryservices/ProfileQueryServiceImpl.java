package com.hampcoders.electrolink.profiles.application.internal.queryservices;

import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetAllProfilesQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByAgeQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByFullNameQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByIdQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfilesByRoleQuery;
import com.hampcoders.electrolink.profiles.domain.services.ProfileQueryService;
import com.hampcoders.electrolink.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the ProfileQueryService interface
 * that handles various queries related to user profiles.
 */
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

  private final ProfileRepository profileRepository;

  /**
   * Constructor for ProfileQueryServiceImpl.
   *
   * @param profileRepository the repository used to access profile data from the database
   */
  public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  /**
   * Handles the GetAllProfilesQuery by fetching all profiles from the database.
   *
   * @param query the query object containing any necessary parameters for fetching profiles
   * @return a list of all profiles in the system
   */
  @Override
  public List<Profile> handle(GetAllProfilesQuery query) {
    return profileRepository.findAll();
  }

  /**
   * Handles the GetProfileByIdQuery by fetching a profile with the specified ID from the database.
   *
   * @param query the query object containing the ID of the profile to fetch
   * @return an Optional containing the profile if found,
   *     or empty if no profile with the specified ID exists
   */
  @Override
  public Optional<Profile> handle(GetProfileByIdQuery query) {
    return profileRepository.findById(query.profileId());
  }

  /**
   * Handles the GetProfileByFullNameQuery by fetching the profiles
   * with the specified first and last name from the database.
   *
   * @param query the query object containing the full name of the profiles to fetch
   * @return the list of profiles matching the full name (empty if none)
   */
  @Override
  public List<Profile> handle(GetProfileByFullNameQuery query) {
    return profileRepository.findByPersonName_FirstNameAndPersonName_LastName(
      query.firstName(), query.lastName()
    );
  }

  /**
   * Handles the GetProfileByEmailQuery by fetching a profile
   * with the specified email address from the database.
   *
   * @param query the query object containing the email of the profile to fetch
   * @return an Optional containing the profile if found,
   *     or empty if no profile with the specified email exists
   */
  @Override
  public Optional<Profile> handle(GetProfileByEmailQuery query) {
    return profileRepository.findByEmail_Address(query.email());
  }

  /**
   * Handles the GetProfilesByRoleQuery by fetching all profiles
   * with the specified role from the database.
   *
   * @param query the query object containing the role for which to fetch profiles
   * @return a list of profiles that have the specified role,
   *     or an empty list if no profiles with that role exist
   */
  @Override
  public List<Profile> handle(GetProfilesByRoleQuery query) {
    return profileRepository.findByRole(query.role());
  }

  /**
   * Handles the GetProfileByAgeQuery by fetching profiles
   * that match the specified age criteria from the database.
   *
   * @param query the query object containing the age criteria for which to fetch profiles
   * @return a list of profiles that match the specified age criteria,
   *     or an empty list if no profiles match
   */
  @Override
  public List<Profile> handle(GetProfileByAgeQuery query) {
    throw new UnsupportedOperationException("Query by age is not supported in current model.");
  }
}
