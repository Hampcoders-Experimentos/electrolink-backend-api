package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindScheduleByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindSchedulesByTechnicianIdQuery;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleQueryServiceImplTest {

  @Mock
  private ScheduleRepository scheduleRepository;

  @InjectMocks
  private ScheduleQueryServiceImpl service;

  @Test
  @DisplayName("handle(FindScheduleByIdQuery) should return Optional with Schedule when it exists (AAA)")
  void handle_FindById_WhenScheduleExists_ShouldReturnOptionalWithSchedule() {
    Long scheduleId = 1L;
    var query = new FindScheduleByIdQuery(scheduleId);
    var expected = mock(ScheduleAggregate.class);
    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(expected));

    var actual = service.handle(query);

    assertTrue(actual.isPresent());
    assertSame(expected, actual.get());
    verify(scheduleRepository, times(1)).findById(scheduleId);
  }

  @Test
  @DisplayName("handle(FindScheduleByIdQuery) should return empty Optional when Schedule does not exist (AAA)")
  void handle_FindById_WhenScheduleDoesNotExist_ShouldReturnEmptyOptional() {
    Long scheduleId = 999L;
    var query = new FindScheduleByIdQuery(scheduleId);
    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

    var actual = service.handle(query);

    assertFalse(actual.isPresent());
    verify(scheduleRepository, times(1)).findById(scheduleId);
  }

  @Test
  @DisplayName("handle(FindSchedulesByTechnicianIdQuery) should return List with multiple Schedules (AAA)")
  void handle_FindByTechnicianId_WhenMultipleSchedulesExist_ShouldReturnList() {
    String technicianId = "TECH-001";
    var scheduleA = mock(ScheduleAggregate.class);
    var scheduleB = mock(ScheduleAggregate.class);
    when(scheduleRepository.findByTechnicianId(technicianId))
        .thenReturn(List.of(scheduleA, scheduleB));
    var query = new FindSchedulesByTechnicianIdQuery(technicianId);

    var actual = service.handle(query);

    assertNotNull(actual);
    assertEquals(2, actual.size());
    assertSame(scheduleA, actual.get(0));
    assertSame(scheduleB, actual.get(1));
    verify(scheduleRepository, times(1)).findByTechnicianId(technicianId);
  }

  @Test
  @DisplayName("handle(FindSchedulesByTechnicianIdQuery) should return empty List when no Schedules (AAA)")
  void handle_FindByTechnicianId_WhenNoSchedulesExist_ShouldReturnEmptyList() {
    String technicianId = "TECH-999";
    when(scheduleRepository.findByTechnicianId(technicianId)).thenReturn(Collections.emptyList());
    var query = new FindSchedulesByTechnicianIdQuery(technicianId);

    var actual = service.handle(query);

    assertNotNull(actual);
    assertTrue(actual.isEmpty());
    verify(scheduleRepository, times(1)).findByTechnicianId(technicianId);
  }

  @Test
  @DisplayName("handle(FindSchedulesByTechnicianIdQuery) should return single Schedule list (AAA)")
  void handle_FindByTechnicianId_WhenSingleScheduleExists_ShouldReturnListWithOne() {
    String technicianId = "TECH-456";
    var schedule = mock(ScheduleAggregate.class);
    when(scheduleRepository.findByTechnicianId(technicianId)).thenReturn(List.of(schedule));
    var query = new FindSchedulesByTechnicianIdQuery(technicianId);

    var actual = service.handle(query);

    assertNotNull(actual);
    assertEquals(1, actual.size());
    assertSame(schedule, actual.get(0));
    verify(scheduleRepository, times(1)).findByTechnicianId(technicianId);
  }
}
