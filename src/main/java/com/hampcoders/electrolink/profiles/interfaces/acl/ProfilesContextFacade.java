package com.hampcoders.electrolink.profiles.interfaces.acl;

import com.hampcoders.electrolink.profiles.domain.model.commands.CreateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.commands.DeleteProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.commands.UpdateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByIdQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfilesByRoleQuery;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.domain.services.ProfileCommandService;
import com.hampcoders.electrolink.profiles.domain.services.ProfileQueryService;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import com.hampcoders.electrolink.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * The ProfilesContextFacade serves as an Application Service
 * that provides a simplified interface for handling profile-related operations.
 */
@Service
public class ProfilesContextFacade {

  private final ProfileCommandService profileCommandService;
  private final ProfileQueryService profileQueryService;

  /**
   * Constructor for ProfilesContextFacade.
   *
   * @param profileCommandService the service responsible for handling profile commands
   * @param profileQueryService the service responsible for handling profile queries
   */
  public ProfilesContextFacade(ProfileCommandService profileCommandService,
                               ProfileQueryService profileQueryService) {
    this.profileCommandService = profileCommandService;
    this.profileQueryService = profileQueryService;
  }

  /**
   * Fetches a profile by its unique identifier.
   *
   * @param profileId the unique identifier of the profile to fetch
   * @return an Optional containing the ProfileResource if found, or empty if not found
   */
  public Optional<ProfileResource> fetchProfileById(Long profileId) {
    var query = new GetProfileByIdQuery(profileId);
    return profileQueryService.handle(query)
      .map(ProfileResourceFromEntityAssembler::toResourceFromEntity);
  }

  /**
   * Fetches a profile by its email address.
   *
   * @param email the email address of the profile to fetch
   * @return an Optional containing the ProfileResource if found, or empty if not found
   */
  public Optional<ProfileResource> fetchProfileByEmail(String email) {
    var query = new GetProfileByEmailQuery(email);
    return profileQueryService.handle(query)
      .map(ProfileResourceFromEntityAssembler::toResourceFromEntity);
  }

  /**
   * Fetches a list of profiles that match the specified role.
   *
   * @param role the role to filter profiles by
   * @return a list of ProfileResource objects matching the specified role
   */
  public List<ProfileResource> fetchProfilesByRole(Role role) {
    var query = new GetProfilesByRoleQuery(role);
    return profileQueryService.handle(query).stream()
      .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
      .toList();
  }

  /**
   * Fetches the unique identifier of a profile based on its email address.
   *
   * @param email the email address of the profile to fetch the ID for
   * @return the unique identifier of the profile if found, or 0L if not found
   */
  public Long fetchProfileIdByEmail(String email) {
    var optional = profileQueryService.handle(new GetProfileByEmailQuery(email));
    return optional.map(AuditableAbstractAggregateRoot::getId).orElse(0L);
  }

  /**
   * Checks if a profile exists with the specified email address,
   * excluding a profile with the given ID.
   *
   * @param email the email address to check for existence
   * @param id the unique identifier of the profile to exclude from the check
   * @return true if a profile exists
   *     with the specified email address and a different ID, false otherwise
   */
  public boolean existsProfileByEmailAndIdIsNot(String email, Long id) {
    var optional = profileQueryService.handle(new GetProfileByEmailQuery(email));
    return optional.isPresent() && !optional.get().getId().equals(id);
  }

  /**
   * Creates a new profile with the provided details and returns its unique identifier.
   *
   * @param firstName the first name of the profile
   * @param lastName the last name of the profile
   * @param email the email address of the profile
   * @param street the street address of the profile
   * @param role the role of the profile
   * @param infoOrCert additional information or certification related to the profile
   * @return the unique identifier of the newly created profile
   */
  public Long createProfile(String firstName, String lastName, String email,
                            String street, Role role, String infoOrCert) {
    var command = new CreateProfileCommand(firstName, lastName, email, street, role, infoOrCert);
    return profileCommandService.handle(command);
  }

  /**
   * Updates an existing profile with the provided details and returns its unique identifier.
   *
   * @param id the unique identifier of the profile to update
   * @param firstName the new first name of the profile
   * @param lastName the new last name of the profile
   * @param email the new email address of the profile
   * @param street the new street address of the profile
   * @param role the new role of the profile
   * @param infoOrCert new additional information or certification related to the profile
   * @return the unique identifier of the updated profile if the update was successful, or
   */
  public Long updateProfile(Long id, String firstName, String lastName,
                            String email, String street, Role role, String infoOrCert) {
    var command = new UpdateProfileCommand(id, firstName, lastName,
        email, street, role, infoOrCert);
    var optional = profileCommandService.handle(command);
    return optional.map(AuditableAbstractAggregateRoot::getId).orElse(0L);
  }

  /**
   * Deletes a profile based on its unique identifier.
   *
   * @param profileId the unique identifier of the profile to delete
   */
  public void deleteProfile(Long profileId) {
    profileCommandService.handle(new DeleteProfileCommand(profileId));
  }

}
