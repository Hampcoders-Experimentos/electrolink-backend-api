package com.hampcoders.electrolink.sdp.application.internal.outboundservices;

import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.interfaces.acl.ProfilesContextFacade;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling external profile-related operations,
 * such as fetching technicians from the Profiles context.
 * This service interacts with the ProfilesContextFacade
 * to perform necessary actions related to profile management for technicians.
 */
@Service
public class ExternalProfileService {

  private final ProfilesContextFacade profilesContextFacade;

  /**
   * External Profile Service Constructor.
   *
   * @param profilesContextFacade The ProfilesContextFacade used to interact
   *     with the profile management system.
   */
  public ExternalProfileService(ProfilesContextFacade profilesContextFacade) {
    this.profilesContextFacade = profilesContextFacade;
  }

  /**
   * Fetches a list of technicians from the Profiles context by using the ProfilesContextFacade.
   *
   * @return A list of ProfileResource objects representing the technicians.
   */
  public List<ProfileResource> fetchTechnicians() {
    return profilesContextFacade.fetchProfilesByRole(Role.TECHNICIAN);
  }
}