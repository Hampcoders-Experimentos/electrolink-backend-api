package com.hampcoders.electrolink.profiles.interfaces.rest;

import com.hampcoders.electrolink.profiles.domain.model.commands.DeleteProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetAllProfilesQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByFullNameQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByIdQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfilesByRoleQuery;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.domain.services.ProfileCommandService;
import com.hampcoders.electrolink.profiles.domain.services.ProfileQueryService;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.CreateProfileResource;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import com.hampcoders.electrolink.profiles.interfaces.rest.transform.CreateProfileCommandFromResourceAssembler;
import com.hampcoders.electrolink.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.hampcoders.electrolink.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing user profiles.
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Profile Management Endpoints")
public class ProfilesController {

  private final ProfileQueryService profileQueryService;
  private final ProfileCommandService profileCommandService;

  /**
   * Constructor for ProfilesController.
   *
   * @param profileQueryService the service for handling profile queries
   * @param profileCommandService the service for handling profile commands
   */
  public ProfilesController(ProfileQueryService profileQueryService,
                            ProfileCommandService profileCommandService) {
    this.profileQueryService = profileQueryService;
    this.profileCommandService = profileCommandService;
  }

  /**
   * Endpoint to create a new profile.
   *
   * @param resource the resource containing profile creation data
   * @return ResponseEntity with the created profile resource or bad request status
   */
  @PostMapping
  public ResponseEntity<ProfileResource> createProfile(@RequestBody
                                                         CreateProfileResource resource) {
    var command = CreateProfileCommandFromResourceAssembler.toCommandFromResource(resource);
    var profileId = profileCommandService.handle(command);

    var optionalProfile = profileQueryService.handle(new GetProfileByIdQuery(profileId));
    return optionalProfile.map(profile ->
        new ResponseEntity<>(ProfileResourceFromEntityAssembler
            .toResourceFromEntity(profile), HttpStatus.CREATED))
      .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  /**
   * Endpoint to retrieve all profiles.
   *
   * @return ResponseEntity with a list of profile resources
   */
  @GetMapping
  public ResponseEntity<List<ProfileResource>> getAllProfiles() {
    var profiles = profileQueryService.handle(new GetAllProfilesQuery());
    var resources = profiles.stream()
        .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
        .collect(Collectors.toList());
    return ResponseEntity.ok(resources);
  }

  /**
   * Endpoint to retrieve a profile by its ID.
   *
   * @param profileId the ID of the profile to retrieve
   * @return ResponseEntity with the profile resource or not found status
   */
  @GetMapping("/{profileId}")
  public ResponseEntity<ProfileResource> getProfileById(@PathVariable Long profileId) {
    var optionalProfile = profileQueryService.handle(new GetProfileByIdQuery(profileId));
    return optionalProfile.map(profile ->
        ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Endpoint to update an existing profile.
   *
   * @param profileId the ID of the profile to update
   * @param resource the resource containing updated profile data
   * @return ResponseEntity with the updated profile resource or bad request status
   */
  @PutMapping("/{profileId}")
  public ResponseEntity<ProfileResource> updateProfile(@PathVariable Long profileId,
                                                       @RequestBody ProfileResource resource) {
    var command = UpdateProfileCommandFromResourceAssembler
        .toCommandFromResource(profileId, resource);
    var optionalProfile = profileCommandService.handle(command);
    return optionalProfile.map(profile ->
        ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
      .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  /**
   * Endpoint to delete a profile by its ID.
   *
   * @param profileId the ID of the profile to delete
   * @return ResponseEntity with no content status
   */
  @DeleteMapping("/{profileId}")
  public ResponseEntity<?> deleteProfile(@PathVariable Long profileId) {
    profileCommandService.handle(new DeleteProfileCommand(profileId));
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint to search for profiles based on optional criteria: email, role, or full name.
   *
   * @param email the email to search for (optional)
   * @param role the role to search for (optional)
   * @param firstName the first name to search for (optional, requires lastName)
   * @param lastName the last name to search for (optional, requires firstName)
   * @return ResponseEntity with a list of matching profile resources
   */
  @GetMapping("/search")
  public ResponseEntity<List<ProfileResource>> searchProfiles(
      @RequestParam(required = false) String email,
      @RequestParam(required = false) Role role,
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName
  ) {
    List<ProfileResource> results;

    if (email != null) {
      var optional = profileQueryService.handle(new GetProfileByEmailQuery(email));
      results = optional.map(profile ->
          List.of(ProfileResourceFromEntityAssembler
              .toResourceFromEntity(profile))).orElseGet(List::of);
    } else if (role != null) {
      results = profileQueryService.handle(new GetProfilesByRoleQuery(role)).stream()
        .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
        .collect(Collectors.toList());
    } else if (firstName != null && lastName != null) {
      var optional = profileQueryService.handle(new GetProfileByFullNameQuery(firstName, lastName));
      results = optional.map(profile ->
            List.of(ProfileResourceFromEntityAssembler
                .toResourceFromEntity(profile))).orElseGet(List::of);
    } else {
      results = profileQueryService.handle(new GetAllProfilesQuery()).stream()
        .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
        .collect(Collectors.toList());
    }

    return ResponseEntity.ok(results);
  }
}
