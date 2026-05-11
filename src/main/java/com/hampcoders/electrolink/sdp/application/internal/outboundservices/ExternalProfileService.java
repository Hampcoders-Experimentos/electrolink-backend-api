package com.hampcoders.electrolink.sdp.application.internal.outboundservices;

import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.interfaces.acl.ProfilesContextFacade;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExternalProfileService implements IExternalProfileService {

    private final ProfilesContextFacade profilesContextFacade;

    public ExternalProfileService(ProfilesContextFacade profilesContextFacade) {
        this.profilesContextFacade = profilesContextFacade;
    }

    @Override
    public List<ProfileResource> fetchTechnicians() {
        return profilesContextFacade.fetchProfilesByRole(Role.TECHNICIAN);
    }
}