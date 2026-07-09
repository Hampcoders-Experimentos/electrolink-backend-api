package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Report;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ReportType;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.ReportResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReportResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a report, when assembling, then it maps id, operation id, description and type")
  void handle_ShouldMapAllFields_WhenReportProvided() {
    // Arrange
    Report report = mock(Report.class);
    when(report.getId()).thenReturn(7L);
    when(report.getServiceOperationId()).thenReturn(10L);
    when(report.getDescription()).thenReturn("desc");
    when(report.getReportType()).thenReturn(ReportType.INCIDENT);

    // Act
    ReportResource resource = ReportResourceFromEntityAssembler.toResourceFromEntity(report);

    // Assert
    assertEquals(7L, resource.id());
    assertEquals(10L, resource.serviceOperationId());
    assertEquals("desc", resource.description());
    assertEquals("INCIDENT", resource.reportType());
  }

  @Test
  @DisplayName("Given another report, when assembling, then it maps the new type")
  void handle_ShouldMapNewType_WhenDifferentReportProvided() {
    // Arrange
    Report report = mock(Report.class);
    when(report.getId()).thenReturn(8L);
    when(report.getServiceOperationId()).thenReturn(11L);
    when(report.getDescription()).thenReturn("done");
    when(report.getReportType()).thenReturn(ReportType.COMPLETION);

    // Act
    ReportResource resource = ReportResourceFromEntityAssembler.toResourceFromEntity(report);

    // Assert
    assertEquals("COMPLETION", resource.reportType());
  }

  @Test
  @DisplayName("Given a null report, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenReportIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> ReportResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
