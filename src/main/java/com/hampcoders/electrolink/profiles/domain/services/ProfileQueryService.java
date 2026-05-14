package com.hampcoders.electrolink.profiles.domain.services;

import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetAllProfilesQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByAgeQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByFullNameQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByIdQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfilesByRoleQuery;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling profile-related queries.
 */
public interface ProfileQueryService {

  /**
   * Handles the GetAllProfilesQuery and returns a list of all profiles.
   *
   * @param query the query object containing any necessary parameters for fetching profiles
   * @return a list of Profile objects matching the query criteria
   */
  List<Profile> handle(GetAllProfilesQuery query);

  /**
   * Handles the GetProfileByIdQuery and returns an Optional containing the profile if found.
   *
   * @param query the query object containing the ID of the profile to fetch
   * @return an Optional containing the Profile if found, or empty if not found
   */
  Optional<Profile> handle(GetProfileByIdQuery query);

  /**
   * Handles the GetProfileByFullNameQuery and returns an Optional containing the profile if found.
   *
   * @param query the query object containing the full name of the profile to fetch
   * @return an Optional containing the Profile if found, or empty if not found
   */
  Optional<Profile> handle(GetProfileByFullNameQuery query);

  /**
   * Handles the GetProfileByEmailQuery and returns an Optional containing the profile if found.
   *
   * @param query the query object containing the email of the profile to fetch
   * @return an Optional containing the Profile if found, or empty if not found
   */
  Optional<Profile> handle(GetProfileByEmailQuery query);

  /**
   * Handles the GetProfilesByRoleQuery and returns a list of profiles matching the specified role.
   *
   * @param query the query object containing the role for which to fetch profiles
   * @return a list of Profile objects that have the specified role
   */
  List<Profile> handle(GetProfilesByRoleQuery query);

  /**
   * Handles the GetProfileByAgeQuery
   * and returns a list of profiles matching the specified age criteria.
   *
   * @param query the query object containing the age criteria for which to fetch profiles
   * @return a list of Profile objects that match the specified age criteria
   */
  List<Profile> handle(GetProfileByAgeQuery query);
}
