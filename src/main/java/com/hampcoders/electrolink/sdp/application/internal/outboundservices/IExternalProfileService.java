package com.hampcoders.electrolink.sdp.application.internal.outboundservices;

import com.hampcoders.electrolink.profiles.interfaces.acl.ProfilesContextFacade;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import java.util.List;

public interface IExternalProfileService {
    List<ProfileResource> fetchTechnicians();
}