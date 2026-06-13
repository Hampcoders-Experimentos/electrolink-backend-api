package com.hampcoders.electrolink.sdp.application.internal.outboundservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import com.hampcoders.electrolink.profiles.interfaces.acl.ProfilesContextFacade;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalProfileServiceTest {

  @Mock
  private ProfilesContextFacade profilesContextFacade;

  @InjectMocks
  private ExternalProfileService externalProfileService;

  @Test
  @DisplayName("Given technicians exist, when fetching technicians, then it returns them")
  void handle_ShouldReturnTechnicians_WhenProfilesExist() {
    // Arrange
    List<ProfileResource> technicians = List.of(new ProfileResource(
        1L, "Jane", "Tech", "jane@mail.com", "Main St", Role.TECHNICIAN, "CERT-1", true));
    when(profilesContextFacade.fetchProfilesByRole(Role.TECHNICIAN)).thenReturn(technicians);

    // Act
    List<ProfileResource> result = externalProfileService.fetchTechnicians();

    // Assert
    assertEquals(technicians, result);
  }

  @Test
  @DisplayName("Given no technicians, when fetching technicians, then it returns an empty list")
  void handle_ShouldReturnEmptyList_WhenNoTechnicians() {
    // Arrange
    when(profilesContextFacade.fetchProfilesByRole(Role.TECHNICIAN)).thenReturn(List.of());

    // Act
    List<ProfileResource> result = externalProfileService.fetchTechnicians();

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a fetch request, when fetching technicians, then it queries the technician role")
  void handle_ShouldQueryTechnicianRole_WhenFetching() {
    // Arrange
    when(profilesContextFacade.fetchProfilesByRole(Role.TECHNICIAN)).thenReturn(List.of());

    // Act
    externalProfileService.fetchTechnicians();

    // Assert
    verify(profilesContextFacade).fetchProfilesByRole(Role.TECHNICIAN);
  }
}
