package com.hampcoders.electrolink.profiles.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetAllProfilesQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByAgeQuery;
import com.hampcoders.electrolink.profiles.domain.model.queries.GetProfileByIdQuery;
import com.hampcoders.electrolink.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileQueryServiceImplTest {

  @Mock
  private ProfileRepository profileRepository;

  @InjectMocks
  private ProfileQueryServiceImpl profileQueryService;

  @Test
  @DisplayName("Given existing profiles, when handling GetAllProfilesQuery, then it returns all of them")
  void handle_ShouldReturnAllProfiles_WhenQueryingAll() {
    // Arrange
    List<Profile> profiles = List.of(mock(Profile.class), mock(Profile.class));
    when(profileRepository.findAll()).thenReturn(profiles);

    // Act
    List<Profile> result = profileQueryService.handle(new GetAllProfilesQuery());

    // Assert
    assertEquals(profiles, result);
  }

  @Test
  @DisplayName("Given a missing id, when handling GetProfileByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenProfileIdMissing() {
    // Arrange
    when(profileRepository.findById(5L)).thenReturn(Optional.empty());

    // Act
    Optional<Profile> result = profileQueryService.handle(new GetProfileByIdQuery(5L));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given an age query, when handling GetProfileByAgeQuery, then it throws UnsupportedOperation")
  void handle_ShouldThrow_WhenQueryingByAge() {
    // Act & Assert
    assertThrows(UnsupportedOperationException.class,
        () -> profileQueryService.handle(new GetProfileByAgeQuery(30)));
  }
}
