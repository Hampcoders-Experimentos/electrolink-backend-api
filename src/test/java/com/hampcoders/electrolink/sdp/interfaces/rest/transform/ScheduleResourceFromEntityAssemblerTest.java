package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.ScheduleResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduleResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a schedule, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenScheduleProvided() {
    // Arrange
    ScheduleAggregate schedule = mock(ScheduleAggregate.class);
    when(schedule.getId()).thenReturn(5L);
    when(schedule.getTechnicianId()).thenReturn("99");
    when(schedule.getDay()).thenReturn("MONDAY");
    when(schedule.getStartTime()).thenReturn("08:00");
    when(schedule.getEndTime()).thenReturn("17:00");

    // Act
    ScheduleResource resource = ScheduleResourceFromEntityAssembler.toResourceFromEntity(schedule);

    // Assert
    assertEquals(5L, resource.id());
    assertEquals("99", resource.technicianId());
    assertEquals("MONDAY", resource.day());
    assertEquals("08:00", resource.startTime());
    assertEquals("17:00", resource.endTime());
  }

  @Test
  @DisplayName("Given another schedule, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentScheduleProvided() {
    // Arrange
    ScheduleAggregate schedule = mock(ScheduleAggregate.class);
    when(schedule.getId()).thenReturn(6L);
    when(schedule.getTechnicianId()).thenReturn("7");
    when(schedule.getDay()).thenReturn("FRIDAY");
    when(schedule.getStartTime()).thenReturn("09:00");
    when(schedule.getEndTime()).thenReturn("13:00");

    // Act
    ScheduleResource resource = ScheduleResourceFromEntityAssembler.toResourceFromEntity(schedule);

    // Assert
    assertEquals(6L, resource.id());
    assertEquals("FRIDAY", resource.day());
  }

  @Test
  @DisplayName("Given a null schedule, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenScheduleIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> ScheduleResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
