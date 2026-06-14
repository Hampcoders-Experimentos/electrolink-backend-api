package com.hampcoders.electrolink.profiles.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.profiles.application.internal.outboundservices.ExternalAssetsService;
import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.commands.CreateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.commands.DeleteProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.commands.UpdateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import com.hampcoders.electrolink.shared.test.util.ReflectionTestUtils;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileCommandServiceImplTest {

  @Mock
  private ProfileRepository profileRepository;
  @Mock
  private ExternalAssetsService externalAssetsService;

  @InjectMocks
  private ProfileCommandServiceImpl profileCommandService;

  private static UpdateProfileCommand updateCommand() {
    return new UpdateProfileCommand(1L, "John", "Doe", "john@mail.com", "Main St",
        Role.HOMEOWNER, "info");
  }

  @Test
  @DisplayName("Given a new homeowner, when handling CreateProfileCommand, then it returns the saved id")
  void handle_ShouldReturnProfileId_WhenHomeownerCreated() {
    // Arrange
    CreateProfileCommand command = new CreateProfileCommand(
        "John", "Doe", "john@mail.com", "Main St", Role.HOMEOWNER, "info");
    when(profileRepository.existsByEmail_Address("john@mail.com")).thenReturn(false);
    when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> {
      Profile profile = invocation.getArgument(0);
      ReflectionTestUtils.setField(profile, "id", 5L);
      return profile;
    });

    // Act
    Long result = profileCommandService.handle(command);

    // Assert
    assertEquals(5L, result);
  }

  @Test
  @DisplayName("Given an existing email, when handling CreateProfileCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenEmailAlreadyExists() {
    // Arrange
    CreateProfileCommand command = new CreateProfileCommand(
        "John", "Doe", "john@mail.com", "Main St", Role.HOMEOWNER, "info");
    when(profileRepository.existsByEmail_Address("john@mail.com")).thenReturn(true);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> profileCommandService.handle(command));
  }

  @Test
  @DisplayName("Given a new technician, when handling CreateProfileCommand, then it creates the inventory")
  void handle_ShouldCreateInventory_WhenTechnicianCreated() {
    // Arrange
    CreateProfileCommand command = new CreateProfileCommand(
        "Jane", "Tech", "jane@mail.com", "Main St", Role.TECHNICIAN, "CERT-1");
    when(profileRepository.existsByEmail_Address("jane@mail.com")).thenReturn(false);
    when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> {
      Profile profile = invocation.getArgument(0);
      ReflectionTestUtils.setField(profile, "id", 7L);
      return profile;
    });

    // Act
    Long result = profileCommandService.handle(command);

    // Assert
    assertEquals(7L, result);
    verify(externalAssetsService).createInventoryForTechnician(7L);
  }

  @Test
  @DisplayName("Given a save failure, when handling CreateProfileCommand, then it wraps the error in IllegalArgument")
  void handle_ShouldWrapError_WhenSaveFailsOnCreate() {
    // Arrange
    CreateProfileCommand command = new CreateProfileCommand(
        "John", "Doe", "john@mail.com", "Main St", Role.HOMEOWNER, "info");
    when(profileRepository.existsByEmail_Address("john@mail.com")).thenReturn(false);
    when(profileRepository.save(any(Profile.class))).thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> profileCommandService.handle(command));
  }

  @Test
  @DisplayName("Given an existing profile, when handling UpdateProfileCommand, then it returns the updated profile")
  void handle_ShouldReturnUpdatedProfile_WhenProfileExists() {
    // Arrange
    Profile profile = mock(Profile.class);
    when(profileRepository.existsById(1L)).thenReturn(true);
    when(profileRepository.existsByEmail_AddressAndIdIsNot("john@mail.com", 1L)).thenReturn(false);
    when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    when(profileRepository.save(profile)).thenReturn(profile);

    // Act
    Optional<Profile> result = profileCommandService.handle(updateCommand());

    // Assert
    assertTrue(result.isPresent());
    assertSame(profile, result.get());
  }

  @Test
  @DisplayName("Given a non-existing profile, when handling UpdateProfileCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenUpdatingNonExistingProfile() {
    // Arrange
    when(profileRepository.existsById(1L)).thenReturn(false);

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> profileCommandService.handle(updateCommand()));
  }

  @Test
  @DisplayName("Given an email used by another profile, when handling UpdateProfileCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenEmailUsedByAnotherProfile() {
    // Arrange
    when(profileRepository.existsById(1L)).thenReturn(true);
    when(profileRepository.existsByEmail_AddressAndIdIsNot("john@mail.com", 1L)).thenReturn(true);

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> profileCommandService.handle(updateCommand()));
  }

  @Test
  @DisplayName("Given an existing profile, when handling DeleteProfileCommand, then it deletes it")
  void handle_ShouldDeleteProfile_WhenProfileExists() {
    // Arrange
    when(profileRepository.existsById(1L)).thenReturn(true);

    // Act
    profileCommandService.handle(new DeleteProfileCommand(1L));

    // Assert
    verify(profileRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Given a non-existing profile, when handling DeleteProfileCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenDeletingNonExistingProfile() {
    // Arrange
    when(profileRepository.existsById(1L)).thenReturn(false);

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> profileCommandService.handle(new DeleteProfileCommand(1L)));
  }

  @Test
  @DisplayName("Given a deletion failure, when handling DeleteProfileCommand, then it wraps the error in IllegalArgument")
  void handle_ShouldWrapError_WhenDeletionFails() {
    // Arrange
    when(profileRepository.existsById(1L)).thenReturn(true);
    doThrow(new RuntimeException("DB error")).when(profileRepository).deleteById(1L);

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> profileCommandService.handle(new DeleteProfileCommand(1L)));
  }
}
