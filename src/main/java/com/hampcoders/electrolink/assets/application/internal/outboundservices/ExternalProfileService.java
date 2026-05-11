package com.hampcoders.electrolink.assets.application.internal.outboundservices;

import com.hampcoders.electrolink.assets.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.profiles.interfaces.acl.ProfilesContextFacade;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Service to interact with the Profiles context via its ACL facade.
 */
@Service
@Component("sdpExternalProfileService")
public class ExternalProfileService {

  private final ProfilesContextFacade profilesContextFacade;

  /**
   * Constructs an ExternalProfileService.
   *
   * @param profilesContextFacade The facade for the Profiles context.
   */
  public ExternalProfileService(final ProfilesContextFacade profilesContextFacade) {
    this.profilesContextFacade = profilesContextFacade;
  }

  /**
   * Fetches the technician ID associated with a given email.
   *
   * @param email The email address to search for.
   * @return An Optional containing the TechnicianId,
     or empty if no profile is found or the ID is 0L.
   */
  public Optional<TechnicianId> fetchTechnicianIdByEmail(final String email) {
    var profileId = profilesContextFacade.fetchProfileIdByEmail(email);
    if (profileId.equals(0L)) {
      return Optional.empty();
    }
    return Optional.of(new TechnicianId(profileId));
  }
}