package com.hampcoders.electrolink.sdp.application.internal.services;

import com.hampcoders.electrolink.assets.interfaces.acl.InventoryContextFacade;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import com.hampcoders.electrolink.sdp.application.internal.outboundservices.ExternalProfileService;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TechnicianMatchingServiceTest {

    @Mock
    private ExternalProfileService externalProfileService;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private InventoryContextFacade inventoryContextFacade;

    @InjectMocks
    private TechnicianMatchingService technicianMatchingService;

    @Test
    @DisplayName("findBestTechnicianForRequest should return empty when no technicians available (AAA)")
    void findBestTechnicianForRequest_ShouldReturnEmpty_WhenNoTechnicians() {
        // Arrange
        Request req = mock(Request.class);
        when(externalProfileService.fetchTechnicians()).thenReturn(List.of());

        // Act
        Optional<String> result = technicianMatchingService.findBestTechnicianForRequest(req);

        // Assert
        assertTrue(result.isEmpty());
        verify(externalProfileService, times(1)).fetchTechnicians();
        verifyNoInteractions(scheduleRepository, inventoryContextFacade);
    }

    @Test
    @DisplayName("findBestTechnicianForRequest should return technician ID when matches all (AAA)")
    void findBestTechnicianForRequest_ShouldReturnTech_WhenMatchesAll() {
        // Arrange
        Request req = mock(Request.class);
        when(req.getScheduledDate()).thenReturn(null); // dayOfWeek will be null, so checkScheduleAvailability is true

        ProfileResource tech = mock(ProfileResource.class);
        when(tech.id()).thenReturn(1L);
        when(externalProfileService.fetchTechnicians()).thenReturn(List.of(tech));

        // checkScheduleAvailability -> dayOfWeek == null -> returns true
        // checkStockAvailability ->
        when(inventoryContextFacade.existsInventoryForTechnician(1L)).thenReturn(true);

        // Act
        Optional<String> result = technicianMatchingService.findBestTechnicianForRequest(req);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("1", result.get());
        verify(inventoryContextFacade, times(1)).existsInventoryForTechnician(1L);
    }

    @Test
    @DisplayName("findBestTechnicianForRequest should return empty if matches schedule but no stock and not priority (AAA)")
    void findBestTechnicianForRequest_ShouldReturnEmpty_WhenNoStockAndNotPriority() {
        // Arrange
        Request req = mock(Request.class);
        when(req.getScheduledDate()).thenReturn(null);
        when(req.isPriority()).thenReturn(false);

        ProfileResource tech = mock(ProfileResource.class);
        when(tech.id()).thenReturn(2L);
        when(externalProfileService.fetchTechnicians()).thenReturn(List.of(tech));

        when(inventoryContextFacade.existsInventoryForTechnician(2L)).thenReturn(false);

        // Act
        Optional<String> result = technicianMatchingService.findBestTechnicianForRequest(req);

        // Assert
        assertTrue(result.isEmpty());
        verify(inventoryContextFacade, times(1)).existsInventoryForTechnician(2L);
    }

    @Test
    @DisplayName("findBestTechnicianForRequest should return tech if matches schedule but no stock AND is priority (AAA)")
    void findBestTechnicianForRequest_ShouldReturnTech_WhenNoStockButPriority() {
        // Arrange
        Request req = mock(Request.class);
        when(req.getScheduledDate()).thenReturn(null);
        when(req.isPriority()).thenReturn(true); // Is priority

        ProfileResource tech = mock(ProfileResource.class);
        when(tech.id()).thenReturn(3L);
        when(externalProfileService.fetchTechnicians()).thenReturn(List.of(tech));

        when(inventoryContextFacade.existsInventoryForTechnician(3L)).thenReturn(false);

        // Act
        Optional<String> result = technicianMatchingService.findBestTechnicianForRequest(req);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("3", result.get());
        verify(inventoryContextFacade, times(1)).existsInventoryForTechnician(3L);
    }
}
