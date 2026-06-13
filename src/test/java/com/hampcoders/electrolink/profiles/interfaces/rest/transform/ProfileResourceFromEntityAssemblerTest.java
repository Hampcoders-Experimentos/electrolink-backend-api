package com.hampcoders.electrolink.profiles.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.entities.HomeOwner;
import com.hampcoders.electrolink.profiles.domain.model.entities.Technician;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.EmailAddress;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.PersonName;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.StreetAddress;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProfileResourceFromEntityAssemblerTest {

  private static Profile baseProfile(Role role) {
    Profile profile = mock(Profile.class);
    when(profile.getId()).thenReturn(1L);
    when(profile.getPersonName()).thenReturn(new PersonName("John", "Doe"));
    when(profile.getEmail()).thenReturn(new EmailAddress("john@mail.com"));
    when(profile.getAddress()).thenReturn(new StreetAddress("Main St"));
    when(profile.getRole()).thenReturn(role);
    return profile;
  }

  @Test
  @DisplayName("Given a homeowner profile, when assembling, then it maps the additional info and null verified")
  void handle_ShouldMapHomeownerInfo_WhenProfileIsHomeowner() {
    // Arrange
    Profile profile = baseProfile(Role.HOMEOWNER);
    HomeOwner homeOwner = mock(HomeOwner.class);
    when(homeOwner.getAdditionalInfo()).thenReturn("extra info");
    when(profile.getHomeOwner()).thenReturn(homeOwner);

    // Act
    ProfileResource resource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile);

    // Assert
    assertEquals(1L, resource.id());
    assertEquals("John", resource.firstName());
    assertEquals("john@mail.com", resource.email());
    assertEquals(Role.HOMEOWNER, resource.role());
    assertEquals("extra info", resource.additionalInfoOrCertification());
    assertNull(resource.isVerified());
  }

  @Test
  @DisplayName("Given a technician profile, when assembling, then it maps the certification and verified flag")
  void handle_ShouldMapTechnicianInfo_WhenProfileIsTechnician() {
    // Arrange
    Profile profile = baseProfile(Role.TECHNICIAN);
    Technician technician = mock(Technician.class);
    when(technician.getCertificationCode()).thenReturn("CERT-1");
    when(technician.getIsVerified()).thenReturn(true);
    when(profile.getTechnician()).thenReturn(technician);

    // Act
    ProfileResource resource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile);

    // Assert
    assertEquals(Role.TECHNICIAN, resource.role());
    assertEquals("CERT-1", resource.additionalInfoOrCertification());
    assertTrue(resource.isVerified());
  }

  @Test
  @DisplayName("Given a null profile, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenProfileIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> ProfileResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
