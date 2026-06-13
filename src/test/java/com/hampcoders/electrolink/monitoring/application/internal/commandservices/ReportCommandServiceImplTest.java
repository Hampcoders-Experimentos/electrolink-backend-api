package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Report;
import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.DeleteReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ReportType;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import com.hampcoders.electrolink.shared.test.util.ReflectionTestUtils;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportCommandServiceImplTest {

  @Mock
  private ReportRepository reportRepository;
  @Mock
  private ServiceOperationRepository serviceOperationRepository;

  @InjectMocks
  private ReportCommandServiceImpl reportCommandService;

  @Test
  @DisplayName("Given an existing service operation, when handling AddReportCommand, then it returns the report id")
  void handle_ShouldReturnReportId_WhenServiceOperationExists() {
    // Arrange
    AddReportCommand command = new AddReportCommand(10L, ReportType.INCIDENT, "desc");
    when(serviceOperationRepository.findById(10L))
        .thenReturn(Optional.of(mock(ServiceOperation.class)));
    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report report = invocation.getArgument(0);
      ReflectionTestUtils.setField(report, "id", 7L);
      return report;
    });

    // Act
    Long result = reportCommandService.handle(command);

    // Assert
    assertEquals(7L, result);
  }

  @Test
  @DisplayName("Given a missing service operation, when handling AddReportCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenServiceOperationMissing() {
    // Arrange
    AddReportCommand command = new AddReportCommand(10L, ReportType.INCIDENT, "desc");
    when(serviceOperationRepository.findById(10L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> reportCommandService.handle(command));
  }

  @Test
  @DisplayName("Given a missing report, when handling DeleteReportCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenDeletingMissingReport() {
    // Arrange
    DeleteReportCommand command = new DeleteReportCommand(5L);
    when(reportRepository.findById(5L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> reportCommandService.handle(command));
  }
}
