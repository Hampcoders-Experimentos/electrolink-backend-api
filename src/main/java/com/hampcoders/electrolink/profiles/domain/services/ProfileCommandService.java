package com.hampcoders.electrolink.profiles.domain.services;

import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.commands.CreateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.commands.DeleteProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.commands.UpdateProfileCommand;
import java.util.Optional;

/**
 * Service interface for handling profile-related commands.
 */
public interface ProfileCommandService {

  /**
   * Handles the creation of a new profile.
   *
   * @param command The command containing the details for creating a profile.
   * @return The ID of the created profile.
   */
  Long handle(CreateProfileCommand command);

  /**
   * Handles the update of an existing profile.
   *
   * @param command The command containing the details for updating a profile.
   * @return An Optional containing the updated profile, or empty if not found.
   */
  Optional<Profile> handle(UpdateProfileCommand command);

  /**
   * Handles the deletion of a profile.
   *
   * @param command The command containing the details for deleting a profile.
   */
  void handle(DeleteProfileCommand command);
}
