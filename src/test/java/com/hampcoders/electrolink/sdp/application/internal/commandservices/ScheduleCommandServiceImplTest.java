package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateScheduleCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteScheduleCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateScheduleCommand;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleCommandServiceImplTest {

  @Mock
  private ScheduleRepository scheduleRepository;

  @InjectMocks
  private ScheduleCommandServiceImpl scheduleCommandService;

  @Test
  @DisplayName("handle(CreateScheduleCommand) should create and save a new schedule successfully (AAA)")
  void handle_CreateSchedule_ShouldSaveAndReturnId() {
    var command = new CreateScheduleCommand("TECH-001", "Monday", "09:00", "17:00");

    var savedSchedule = mock(ScheduleAggregate.class);
    when(savedSchedule.getId()).thenReturn(1L);
    when(scheduleRepository.save(any(ScheduleAggregate.class))).thenReturn(savedSchedule);

    Long scheduleId = scheduleCommandService.handle(command);

    assertNotNull(scheduleId);
    assertEquals(1L, scheduleId);

    var captor = ArgumentCaptor.forClass(ScheduleAggregate.class);
    verify(scheduleRepository, times(1)).save(captor.capture());

    var capturedSchedule = captor.getValue();
    assertEquals("TECH-001", capturedSchedule.getTechnicianId());
    assertEquals("Monday", capturedSchedule.getDay());
    assertEquals("09:00", capturedSchedule.getStartTime());
    assertEquals("17:00", capturedSchedule.getEndTime());
  }

  @Test
  @DisplayName("handle(CreateScheduleCommand) should create schedule with valid data (AAA)")
  void handle_CreateSchedule_WithValidData_ShouldCreateCorrectSchedule() {
    var command = new CreateScheduleCommand("TECH-456", "Friday", "08:00", "16:00");

    var savedSchedule = mock(ScheduleAggregate.class);
    when(savedSchedule.getId()).thenReturn(10L);
    when(scheduleRepository.save(any(ScheduleAggregate.class))).thenReturn(savedSchedule);

    Long scheduleId = scheduleCommandService.handle(command);

    assertNotNull(scheduleId);
    assertEquals(10L, scheduleId);
    verify(scheduleRepository, times(1)).save(any(ScheduleAggregate.class));
  }

  @Test
  @DisplayName("handle(UpdateScheduleCommand) should update existing schedule successfully (AAA)")
  void handle_UpdateSchedule_WhenScheduleExists_ShouldUpdateSuccessfully() {
    Long scheduleId = 1L;
    var command = new UpdateScheduleCommand(scheduleId, "TECH-001", "Tuesday", "10:00", "18:00");

    var existingSchedule = mock(ScheduleAggregate.class);
    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existingSchedule));
    when(scheduleRepository.save(any(ScheduleAggregate.class))).thenReturn(existingSchedule);

    scheduleCommandService.handle(command);

    verify(existingSchedule, times(1)).updateFrom(command);
    verify(scheduleRepository, times(1)).findById(scheduleId);
    verify(scheduleRepository, times(1)).save(existingSchedule);
  }

  @Test
  @DisplayName("handle(UpdateScheduleCommand) should throw exception when schedule does not exist (AAA)")
  void handle_UpdateSchedule_WhenScheduleDoesNotExist_ShouldThrowException() {
    Long scheduleId = 999L;
    var command = new UpdateScheduleCommand(scheduleId, "TECH-001", "Wednesday", "09:00", "17:00");

    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

    var exception = assertThrows(IllegalArgumentException.class,
        () -> scheduleCommandService.handle(command));
    assertEquals("Schedule not found", exception.getMessage());
    verify(scheduleRepository, never()).save(any());
  }

  @Test
  @DisplayName("handle(DeleteScheduleCommand) should delete existing schedule successfully (AAA)")
  void handle_DeleteSchedule_WhenScheduleExists_ShouldDeleteSuccessfully() {
    Long scheduleId = 1L;
    var command = new DeleteScheduleCommand(scheduleId);

    when(scheduleRepository.existsById(scheduleId)).thenReturn(true);
    doNothing().when(scheduleRepository).deleteById(scheduleId);

    scheduleCommandService.handle(command);

    verify(scheduleRepository, times(1)).existsById(scheduleId);
    verify(scheduleRepository, times(1)).deleteById(scheduleId);
  }

  @Test
  @DisplayName("handle(DeleteScheduleCommand) should throw exception when schedule does not exist (AAA)")
  void handle_DeleteSchedule_WhenScheduleDoesNotExist_ShouldThrowException() {
    Long scheduleId = 999L;
    var command = new DeleteScheduleCommand(scheduleId);

    when(scheduleRepository.existsById(scheduleId)).thenReturn(false);

    var exception = assertThrows(IllegalArgumentException.class,
        () -> scheduleCommandService.handle(command));
    assertEquals("Schedule not found", exception.getMessage());
    verify(scheduleRepository, never()).deleteById(any());
  }
}
