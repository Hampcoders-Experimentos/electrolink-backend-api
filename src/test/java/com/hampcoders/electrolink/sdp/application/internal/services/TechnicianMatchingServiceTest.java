package com.hampcoders.electrolink.sdp.application.internal.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.interfaces.acl.InventoryContextFacade;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import com.hampcoders.electrolink.sdp.application.internal.outboundservices.ExternalProfileService;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TechnicianMatchingServiceTest {

  private static final LocalDate SCHEDULED_DATE = LocalDate.of(2026, 6, 15);
  private static final String SCHEDULED_DAY = SCHEDULED_DATE.getDayOfWeek().name();

  @Mock
  private ExternalProfileService externalProfileService;
  @Mock
  private ScheduleRepository scheduleRepository;
  @Mock
  private InventoryContextFacade inventoryContextFacade;

  @InjectMocks
  private TechnicianMatchingService technicianMatchingService;

  private static ProfileResource technician(Long id) {
    return new ProfileResource(id, "Jane", "Tech", "jane@mail.com", "Main St",
        Role.TECHNICIAN, "CERT-1", true);
  }

  @Test
  @DisplayName("Given no technicians, when finding the best technician, then it returns empty")
  void handle_ShouldReturnEmpty_WhenNoTechniciansAvailable() {
    // Arrange
    when(externalProfileService.fetchTechnicians()).thenReturn(List.of());

    // Act
    Optional<String> result =
        technicianMatchingService.findBestTechnicianForRequest(mock(Request.class));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a technician matching schedule and stock, when finding the best technician, then it returns the id")
  void handle_ShouldReturnTechnicianId_WhenScheduleAndStockMatch() {
    // Arrange
    Request request = mock(Request.class);
    when(request.getScheduledDate()).thenReturn(SCHEDULED_DATE);
    ScheduleAggregate schedule = mock(ScheduleAggregate.class);
    when(schedule.getDay()).thenReturn(SCHEDULED_DAY);
    when(externalProfileService.fetchTechnicians()).thenReturn(List.of(technician(7L)));
    when(scheduleRepository.findByTechnicianId("7")).thenReturn(List.of(schedule));
    when(inventoryContextFacade.existsInventoryForTechnician(7L)).thenReturn(true);

    // Act
    Optional<String> result = technicianMatchingService.findBestTechnicianForRequest(request);

    // Assert
    assertEquals(Optional.of("7"), result);
  }

  @Test
  @DisplayName("Given no match and a non-priority request, when finding the best technician, then it returns empty")
  void handle_ShouldReturnEmpty_WhenNoMatchAndNotPriority() {
    // Arrange
    Request request = mock(Request.class);
    when(request.getScheduledDate()).thenReturn(SCHEDULED_DATE);
    when(request.isPriority()).thenReturn(false);
    ScheduleAggregate schedule = mock(ScheduleAggregate.class);
    when(schedule.getDay()).thenReturn("NON_MATCHING_DAY");
    when(externalProfileService.fetchTechnicians()).thenReturn(List.of(technician(7L)));
    when(scheduleRepository.findByTechnicianId("7")).thenReturn(List.of(schedule));

    // Act
    Optional<String> result = technicianMatchingService.findBestTechnicianForRequest(request);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a priority request without stock, when finding the best technician, then it falls back to schedule only")
  void handle_ShouldReturnTechnicianId_WhenPriorityFallbackMatchesSchedule() {
    // Arrange
    Request request = mock(Request.class);
    when(request.getScheduledDate()).thenReturn(SCHEDULED_DATE);
    when(request.isPriority()).thenReturn(true);
    ScheduleAggregate schedule = mock(ScheduleAggregate.class);
    when(schedule.getDay()).thenReturn(SCHEDULED_DAY);
    when(externalProfileService.fetchTechnicians()).thenReturn(List.of(technician(7L)));
    when(scheduleRepository.findByTechnicianId("7")).thenReturn(List.of(schedule));
    when(inventoryContextFacade.existsInventoryForTechnician(7L)).thenReturn(false);

    // Act
    Optional<String> result = technicianMatchingService.findBestTechnicianForRequest(request);

    // Assert
    assertEquals(Optional.of("7"), result);
  }
}
