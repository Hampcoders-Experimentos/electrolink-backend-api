package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateScheduleCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteScheduleCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateScheduleCommand;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleCommandServiceImplTest {

  @Mock
  private ScheduleRepository scheduleRepository;

  @InjectMocks
  private ScheduleCommandServiceImpl scheduleCommandService;

  @Test
  @DisplayName("Given a valid command, when handling CreateScheduleCommand, then it returns the saved id")
  void handle_ShouldReturnScheduleId_WhenCreated() {
    // Arrange
    CreateScheduleCommand command =
        new CreateScheduleCommand("99", "MONDAY", "08:00", "17:00");
    ScheduleAggregate saved = mock(ScheduleAggregate.class);
    when(saved.getId()).thenReturn(5L);
    when(scheduleRepository.save(any(ScheduleAggregate.class))).thenReturn(saved);

    // Act
    Long result = scheduleCommandService.handle(command);

    // Assert
    assertEquals(5L, result);
  }

  @Test
  @DisplayName("Given a missing schedule, when handling UpdateScheduleCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenUpdatingMissingSchedule() {
    // Arrange
    UpdateScheduleCommand command =
        new UpdateScheduleCommand(10L, "99", "MONDAY", "08:00", "17:00");
    when(scheduleRepository.findById(10L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> scheduleCommandService.handle(command));
  }

  @Test
  @DisplayName("Given a missing schedule, when handling DeleteScheduleCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenDeletingMissingSchedule() {
    // Arrange
    DeleteScheduleCommand command = new DeleteScheduleCommand(7L);
    when(scheduleRepository.existsById(7L)).thenReturn(false);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> scheduleCommandService.handle(command));
  }
}
