package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateScheduleCommand;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.UpdateScheduleResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateScheduleCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource and id, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    UpdateScheduleResource resource = new UpdateScheduleResource("99", "MONDAY", "08:00", "17:00");

    // Act
    UpdateScheduleCommand command =
        UpdateScheduleCommandFromResourceAssembler.toCommandFromResource(5L, resource);

    // Assert
    assertEquals(5L, command.scheduleId());
    assertEquals("99", command.technicianId());
    assertEquals("MONDAY", command.day());
    assertEquals("08:00", command.startTime());
    assertEquals("17:00", command.endTime());
  }

  @Test
  @DisplayName("Given a different id, when assembling, then it maps the new id")
  void handle_ShouldMapNewId_WhenDifferentIdProvided() {
    // Arrange
    UpdateScheduleResource resource = new UpdateScheduleResource("7", "FRIDAY", "09:00", "13:00");

    // Act
    UpdateScheduleCommand command =
        UpdateScheduleCommandFromResourceAssembler.toCommandFromResource(9L, resource);

    // Assert
    assertEquals(9L, command.scheduleId());
    assertEquals("FRIDAY", command.day());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> UpdateScheduleCommandFromResourceAssembler.toCommandFromResource(5L, null));
  }
}
