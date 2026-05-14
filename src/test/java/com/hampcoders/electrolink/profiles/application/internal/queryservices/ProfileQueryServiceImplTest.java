package com.hampcoders.electrolink.profiles.application.internal.queryservices;

import com.hampcoders.electrolink.profiles.domain.model.aggregates.Profile;
import com.hampcoders.electrolink.profiles.domain.model.queries.*;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileQueryServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileQueryServiceImpl queryService;

    @Test
    @DisplayName("handle(GetAllProfilesQuery) should return all profiles (AAA)")
    void handleGetAllProfiles_ShouldReturnList() {
        // Arrange
        GetAllProfilesQuery query = new GetAllProfilesQuery();
        Profile p1 = mock(Profile.class);
        Profile p2 = mock(Profile.class);
        when(profileRepository.findAll()).thenReturn(List.of(p1, p2));

        // Act
        List<Profile> result = queryService.handle(query);

        // Assert
        assertEquals(2, result.size());
        verify(profileRepository).findAll();
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    @DisplayName("handle(GetProfileByIdQuery) should return profile when found (AAA)")
    void handleGetProfileById_ShouldReturnOptional() {
        // Arrange
        Long profileId = 1L;
        GetProfileByIdQuery query = new GetProfileByIdQuery(profileId);
        Profile expected = mock(Profile.class);
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(expected));

        // Act
        Optional<Profile> result = queryService.handle(query);

        // Assert
        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(profileRepository).findById(profileId);
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    @DisplayName("handle(GetProfileByFullNameQuery) should return profile when found (AAA)")
    void handleGetProfileByFullName_ShouldReturnOptional() {
        // Arrange
        GetProfileByFullNameQuery query = new GetProfileByFullNameQuery("Juan", "Perez");
        Profile expected = mock(Profile.class);
        when(profileRepository.findByPersonName_FirstNameAndPersonName_LastName("Juan", "Perez"))
                .thenReturn(Optional.of(expected));

        // Act
        Optional<Profile> result = queryService.handle(query);

        // Assert
        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(profileRepository).findByPersonName_FirstNameAndPersonName_LastName("Juan", "Perez");
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    @DisplayName("handle(GetProfileByEmailQuery) should return profile when found (AAA)")
    void handleGetProfileByEmail_ShouldReturnOptional() {
        // Arrange
        GetProfileByEmailQuery query = new GetProfileByEmailQuery("test@test.com");
        Profile expected = mock(Profile.class);
        when(profileRepository.findByEmail_Address("test@test.com")).thenReturn(Optional.of(expected));

        // Act
        Optional<Profile> result = queryService.handle(query);

        // Assert
        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(profileRepository).findByEmail_Address("test@test.com");
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    @DisplayName("handle(GetProfilesByRoleQuery) should return profiles for specific role (AAA)")
    void handleGetProfilesByRole_ShouldReturnList() {
        // Arrange
        GetProfilesByRoleQuery query = new GetProfilesByRoleQuery(Role.TECHNICIAN);
        Profile p1 = mock(Profile.class);
        when(profileRepository.findByRole(Role.TECHNICIAN)).thenReturn(List.of(p1));

        // Act
        List<Profile> result = queryService.handle(query);

        // Assert
        assertEquals(1, result.size());
        verify(profileRepository).findByRole(Role.TECHNICIAN);
        verifyNoMoreInteractions(profileRepository);
    }

    @Test
    @DisplayName("handle(GetProfileByAgeQuery) should throw UnsupportedOperationException (AAA)")
    void handleGetProfileByAge_ShouldThrowException() {
        // Arrange
        GetProfileByAgeQuery query = new GetProfileByAgeQuery(30);

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> queryService.handle(query));
        verifyNoInteractions(profileRepository);
    }
}
