package com.hampcoders.electrolink.profiles.interfaces.acl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.commands.CreateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.commands.DeleteProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.commands.UpdateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByIdQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfilesByRoleQuery;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.EmailAddress;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.PersonName;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.StreetAddress;
import com.hampcoders.electrolink.profiles.domain.services.ProfileCommandService;
import com.hampcoders.electrolink.profiles.domain.services.ProfileQueryService;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import java.util.List;
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

  private static Profile homeownerProfile() {
    Profile profile = mock(Profile.class);
    when(profile.getId()).thenReturn(5L);
    when(profile.getRole()).thenReturn(Role.HOMEOWNER);
    when(profile.getHomeOwner()).thenReturn(null);
    when(profile.getPersonName()).thenReturn(new PersonName("John", "Doe"));
    when(profile.getEmail()).thenReturn(new EmailAddress("john@mail.com"));
    when(profile.getAddress()).thenReturn(new StreetAddress("Main St"));
    return profile;
  }

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

  @Test
  @DisplayName("Given an existing profile, when fetching by id, then it returns the resource")
  void handle_ShouldReturnResource_WhenFetchingById() {
    // Arrange
    Profile profile = homeownerProfile();
    when(profileQueryService.handle(any(GetProfileByIdQuery.class)))
        .thenReturn(Optional.of(profile));

    // Act
    Optional<ProfileResource> result = profilesContextFacade.fetchProfileById(5L);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(5L, result.get().id());
  }

  @Test
  @DisplayName("Given an existing profile, when fetching by email, then it returns the resource")
  void handle_ShouldReturnResource_WhenFetchingByEmail() {
    // Arrange
    Profile profile = homeownerProfile();
    when(profileQueryService.handle(any(GetProfileByEmailQuery.class)))
        .thenReturn(Optional.of(profile));

    // Act
    Optional<ProfileResource> result = profilesContextFacade.fetchProfileByEmail("john@mail.com");

    // Assert
    assertTrue(result.isPresent());
    assertEquals("john@mail.com", result.get().email());
  }

  @Test
  @DisplayName("Given profiles with a role, when fetching by role, then it returns the resources")
  void handle_ShouldReturnResources_WhenFetchingByRole() {
    // Arrange
    Profile profile = homeownerProfile();
    when(profileQueryService.handle(any(GetProfilesByRoleQuery.class)))
        .thenReturn(List.of(profile));

    // Act
    List<ProfileResource> result = profilesContextFacade.fetchProfilesByRole(Role.HOMEOWNER);

    // Assert
    assertEquals(1, result.size());
    assertEquals(5L, result.get(0).id());
  }

  @Test
  @DisplayName("Given a profile with a different id, when checking existence, then it returns true")
  void handle_ShouldReturnTrue_WhenEmailUsedByDifferentProfile() {
    // Arrange
    Profile profile = mock(Profile.class);
    when(profile.getId()).thenReturn(5L);
    when(profileQueryService.handle(any(GetProfileByEmailQuery.class)))
        .thenReturn(Optional.of(profile));

    // Act
    boolean result = profilesContextFacade.existsProfileByEmailAndIdIsNot("john@mail.com", 9L);

    // Assert
    assertTrue(result);
  }

  @Test
  @DisplayName("Given a profile with the same id, when checking existence, then it returns false")
  void handle_ShouldReturnFalse_WhenEmailBelongsToSameProfile() {
    // Arrange
    Profile profile = mock(Profile.class);
    when(profile.getId()).thenReturn(9L);
    when(profileQueryService.handle(any(GetProfileByEmailQuery.class)))
        .thenReturn(Optional.of(profile));

    // Act
    boolean result = profilesContextFacade.existsProfileByEmailAndIdIsNot("john@mail.com", 9L);

    // Assert
    assertFalse(result);
  }

  @Test
  @DisplayName("Given a successful update, when updating a profile, then it returns the profile id")
  void handle_ShouldReturnProfileId_WhenUpdatingProfile() {
    // Arrange
    Profile profile = mock(Profile.class);
    when(profile.getId()).thenReturn(5L);
    when(profileCommandService.handle(any(UpdateProfileCommand.class)))
        .thenReturn(Optional.of(profile));

    // Act
    Long result = profilesContextFacade.updateProfile(
        5L, "John", "Doe", "john@mail.com", "Main St", Role.HOMEOWNER, "info");

    // Assert
    assertEquals(5L, result);
  }

  @Test
  @DisplayName("Given a profile id, when deleting a profile, then it delegates a delete command")
  void handle_ShouldDelegateDelete_WhenDeletingProfile() {
    // Act
    profilesContextFacade.deleteProfile(5L);

    // Assert
    verify(profileCommandService).handle(any(DeleteProfileCommand.class));
  }
}
