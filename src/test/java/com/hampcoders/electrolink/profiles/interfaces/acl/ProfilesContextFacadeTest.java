package com.hampcoders.electrolink.profiles.interfaces.acl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.commands.CreateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.domain.services.ProfileCommandService;
import com.hampcoders.electrolink.profiles.domain.services.ProfileQueryService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfilesContextFacadeTest {

  @Mock
  private ProfileCommandService profileCommandService;
  @Mock
  private ProfileQueryService profileQueryService;

  @InjectMocks
  private ProfilesContextFacade profilesContextFacade;

  @Test
  @DisplayName("Given an existing email, when fetching the profile id by email, then it returns the id")
  void handle_ShouldReturnProfileId_WhenEmailFound() {
    // Arrange
    Profile profile = mock(Profile.class);
    when(profile.getId()).thenReturn(5L);
    when(profileQueryService.handle(any(GetProfileByEmailQuery.class)))
        .thenReturn(Optional.of(profile));

    // Act
    Long result = profilesContextFacade.fetchProfileIdByEmail("john@mail.com");

    // Assert
    assertEquals(5L, result);
  }

  @Test
  @DisplayName("Given a missing email, when fetching the profile id by email, then it returns zero")
  void handle_ShouldReturnZero_WhenEmailNotFound() {
    // Arrange
    when(profileQueryService.handle(any(GetProfileByEmailQuery.class)))
        .thenReturn(Optional.empty());

    // Act
    Long result = profilesContextFacade.fetchProfileIdByEmail("missing@mail.com");

    // Assert
    assertEquals(0L, result);
  }

  @Test
  @DisplayName("Given profile details, when creating a profile, then it returns the new profile id")
  void handle_ShouldReturnProfileId_WhenCreatingProfile() {
    // Arrange
    when(profileCommandService.handle(any(CreateProfileCommand.class))).thenReturn(9L);

    // Act
    Long result = profilesContextFacade.createProfile(
        "John", "Doe", "john@mail.com", "Main St", Role.HOMEOWNER, "info");

    // Assert
    assertEquals(9L, result);
  }
}
