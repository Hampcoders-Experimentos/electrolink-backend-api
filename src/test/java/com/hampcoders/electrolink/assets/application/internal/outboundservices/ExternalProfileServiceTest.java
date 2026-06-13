package com.hampcoders.electrolink.assets.application.internal.outboundservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.profiles.interfaces.acl.ProfilesContextFacade;
import java.util.Optional;
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
  @DisplayName("Given a profile exists, when fetching technician id by email, then it returns the technician id")
  void handle_ShouldReturnTechnicianId_WhenProfileExists() {
    // Arrange
    when(profilesContextFacade.fetchProfileIdByEmail("tech@mail.com")).thenReturn(9L);

    // Act
    Optional<TechnicianId> result =
        externalProfileService.fetchTechnicianIdByEmail("tech@mail.com");

    // Assert
    assertEquals(Optional.of(new TechnicianId(9L)), result);
  }

  @Test
  @DisplayName("Given a profile id of zero, when fetching technician id by email, then it returns empty")
  void handle_ShouldReturnEmpty_WhenProfileIdIsZero() {
    // Arrange
    when(profilesContextFacade.fetchProfileIdByEmail("missing@mail.com")).thenReturn(0L);

    // Act
    Optional<TechnicianId> result =
        externalProfileService.fetchTechnicianIdByEmail("missing@mail.com");

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a different profile id, when fetching technician id by email, then it wraps that id")
  void handle_ShouldReturnTechnicianId_WhenProfileIdIsDifferent() {
    // Arrange
    when(profilesContextFacade.fetchProfileIdByEmail("other@mail.com")).thenReturn(42L);

    // Act
    Optional<TechnicianId> result =
        externalProfileService.fetchTechnicianIdByEmail("other@mail.com");

    // Assert
    assertEquals(Optional.of(new TechnicianId(42L)), result);
  }
}
