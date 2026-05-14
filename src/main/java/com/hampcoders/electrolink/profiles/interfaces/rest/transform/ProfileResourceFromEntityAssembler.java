package com.hampcoders.electrolink.profiles.interfaces.rest.transform;

import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;

/**
 * This class is responsible for transforming a Profile entity into a ProfileResource.
 */
public class ProfileResourceFromEntityAssembler {

  /**
   * Transforms a Profile entity into a ProfileResource.
   *
   * @param entity The Profile entity to be transformed.
   * @return A ProfileResource containing the data from the Profile entity.
   */
  public static ProfileResource toResourceFromEntity(Profile entity) {
    String info = null;
    Boolean isVerified = null;
    switch (entity.getRole()) {
      case HOMEOWNER -> {
        if (entity.getHomeOwner() != null) {
          info = entity.getHomeOwner().getAdditionalInfo();
        }
      }
      case TECHNICIAN -> {
        if (entity.getTechnician() != null) {
          info = entity.getTechnician().getCertificationCode();
          isVerified = entity.getTechnician().getIsVerified();
        }
      }
      default -> {
        info = entity.getRole().toString();
      }
    }

    return new ProfileResource(
      entity.getId(),
      entity.getPersonName().firstName(),
      entity.getPersonName().lastName(),
      entity.getEmail().address(),
      entity.getAddress().street(),
      entity.getRole(),
      info,
      isVerified
    );
  }
}
