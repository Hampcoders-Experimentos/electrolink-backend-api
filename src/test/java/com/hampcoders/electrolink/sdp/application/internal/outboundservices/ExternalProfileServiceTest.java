package com.hampcoders.electrolink.sdp.application.internal.outboundservices;

import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.interfaces.acl.ProfilesContextFacade;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExternalProfileServiceTest {

    @Mock
    private ProfilesContextFacade profilesContextFacade;

    @InjectMocks
    private ExternalProfileService externalProfileService;

    @Test
    @DisplayName("fetchTechnicians should call ProfilesContextFacade with TECHNICIAN role (AAA)")
    void fetchTechnicians_ShouldReturnList() {
        // Arrange
        ProfileResource tech = mock(ProfileResource.class);
        when(profilesContextFacade.fetchProfilesByRole(Role.TECHNICIAN)).thenReturn(List.of(tech));

        // Act
        List<ProfileResource> result = externalProfileService.fetchTechnicians();

        // Assert
        assertEquals(1, result.size());
        assertEquals(tech, result.get(0));
        verify(profilesContextFacade, times(1)).fetchProfilesByRole(Role.TECHNICIAN);
        verifyNoMoreInteractions(profilesContextFacade);
    }
}
