package com.hampcoders.electrolink.profiles.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.profiles.application.internal.outboundservices.ExternalAssetsService;
import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.commands.CreateProfileCommand;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import com.hampcoders.electrolink.shared.test.util.ReflectionTestUtils;
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
}
