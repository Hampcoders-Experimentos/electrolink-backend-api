package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindScheduleByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindSchedulesByTechnicianIdQuery;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleQueryServiceImplTest {

  @Mock
  private ScheduleRepository scheduleRepository;

  @InjectMocks
  private ScheduleQueryServiceImpl scheduleQueryService;

  @Test
  @DisplayName("Given an existing id, when handling FindScheduleByIdQuery, then it returns the schedule")
  void handle_ShouldReturnSchedule_WhenIdExists() {
    // Arrange
    ScheduleAggregate schedule = mock(ScheduleAggregate.class);
    when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

    // Act
    Optional<ScheduleAggregate> result = scheduleQueryService.handle(new FindScheduleByIdQuery(1L));

    // Assert
    assertTrue(result.isPresent());
    assertSame(schedule, result.get());
  }

  @Test
  @DisplayName("Given a missing id, when handling FindScheduleByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenIdMissing() {
    // Arrange
    when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

    // Act
    Optional<ScheduleAggregate> result = scheduleQueryService.handle(new FindScheduleByIdQuery(1L));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a technician with schedules, when handling FindSchedulesByTechnicianIdQuery, then it returns them")
  void handle_ShouldReturnSchedulesByTechnician_WhenTechnicianHasSchedules() {
    // Arrange
    List<ScheduleAggregate> schedules = List.of(mock(ScheduleAggregate.class));
    when(scheduleRepository.findByTechnicianId("99")).thenReturn(schedules);

    // Act
    List<ScheduleAggregate> result =
        scheduleQueryService.handle(new FindSchedulesByTechnicianIdQuery("99"));

    // Assert
    assertEquals(schedules, result);
  }
}
