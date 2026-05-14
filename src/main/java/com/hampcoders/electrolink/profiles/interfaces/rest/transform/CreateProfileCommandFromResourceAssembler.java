package com.hampcoders.electrolink.profiles.interfaces.rest.transform;

import com.hampcoders.electrolink.profiles.domain.model.commands.CreateProfileCommand;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.CreateProfileResource;

/**
 * Assembler class responsible for converting
 * CreateProfileResource objects into CreateProfileCommand objects.
 */
public class CreateProfileCommandFromResourceAssembler {

  /**
   * Converts a CreateProfileResource object into a CreateProfileCommand object.
   *
   * @param resource the CreateProfileResource object to convert
   * @return the resulting CreateProfileCommand object
   */
  public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
    return new CreateProfileCommand(
      resource.firstName(),
      resource.lastName(),
      resource.email(),
      resource.street(),
      resource.role(),
      resource.additionalInfoOrCertification()
    );
  }
}
