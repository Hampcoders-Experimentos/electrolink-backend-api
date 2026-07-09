package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.monitoring.domain.model.commands.AddReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ReportType;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.CreateReportResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateReportCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given an INCIDENT resource, when assembling, then it maps the report type")
  void handle_ShouldMapFields_WhenReportTypeIsValid() {
    // Arrange
    CreateReportResource resource = new CreateReportResource(10L, "INCIDENT", "desc");

    // Act
    AddReportCommand command =
        CreateReportCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(10L, command.serviceOperationId());
    assertEquals(ReportType.INCIDENT, command.reportType());
    assertEquals("desc", command.description());
  }

  @Test
  @DisplayName("Given a COMPLETION resource, when assembling, then it maps the completion type")
  void handle_ShouldMapCompletionType_WhenReportTypeIsCompletion() {
    // Arrange
    CreateReportResource resource = new CreateReportResource(11L, "COMPLETION", "done");

    // Act
    AddReportCommand command =
        CreateReportCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(ReportType.COMPLETION, command.reportType());
  }

  @Test
  @DisplayName("Given an unknown report type, when assembling, then it throws IllegalArgumentException")
  void handle_ShouldThrowIllegalArgument_WhenReportTypeIsInvalid() {
    // Arrange
    CreateReportResource resource = new CreateReportResource(10L, "UNKNOWN", "desc");

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> CreateReportCommandFromResourceAssembler.toCommandFromResource(resource));
  }
}
