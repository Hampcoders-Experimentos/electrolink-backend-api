package com.hampcoders.electrolink.profiles.interfaces.rest.transform;

import com.hampcoders.electrolink.profiles.domain.model.commands.UpdateProfileCommand;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;

/**
 * Assembler class that transforms a ProfileResource into an UpdateProfileCommand.
 */
public class UpdateProfileCommandFromResourceAssembler {

  /**
   * Converts a ProfileResource into an UpdateProfileCommand, using the provided profileId.
   *
   * @param profileId the ID of the profile to be updated
   * @param resource the ProfileResource containing the new profile data
   * @return an UpdateProfileCommand with the data from the resource and the provided profileId
   */
  public static UpdateProfileCommand toCommandFromResource(Long profileId,
                                                           ProfileResource resource) {
    return new UpdateProfileCommand(
        profileId,
      resource.firstName(),
      resource.lastName(),
      resource.email(),
      resource.street(),
      resource.role(),
      resource.additionalInfoOrCertification()
    );
  }
}
