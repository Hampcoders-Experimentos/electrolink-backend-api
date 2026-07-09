package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.sdp.domain.model.commands.CreateScheduleCommand;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateScheduleResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateScheduleCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    CreateScheduleResource resource = new CreateScheduleResource("99", "MONDAY", "08:00", "17:00");

    // Act
    CreateScheduleCommand command =
        CreateScheduleCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals("99", command.technicianId());
    assertEquals("MONDAY", command.day());
    assertEquals("08:00", command.startTime());
    assertEquals("17:00", command.endTime());
  }

  @Test
  @DisplayName("Given different values, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentResourceProvided() {
    // Arrange
    CreateScheduleResource resource = new CreateScheduleResource("7", "FRIDAY", "09:00", "13:00");

    // Act
    CreateScheduleCommand command =
        CreateScheduleCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals("7", command.technicianId());
    assertEquals("FRIDAY", command.day());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreateScheduleCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
