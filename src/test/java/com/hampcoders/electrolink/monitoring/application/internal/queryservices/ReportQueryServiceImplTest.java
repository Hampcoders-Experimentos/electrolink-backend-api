package com.hampcoders.electrolink.monitoring.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Report;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetAllReportsQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetReportByIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetReportsByServiceOperationIdQuery;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportQueryServiceImplTest {

  @Mock
  private ReportRepository reportRepository;

  @InjectMocks
  private ReportQueryServiceImpl reportQueryService;

  @Test
  @DisplayName("Given existing reports, when handling GetAllReportsQuery, then it returns all of them")
  void handle_ShouldReturnAllReports_WhenQueryingAll() {
    // Arrange
    List<Report> reports = List.of(mock(Report.class));
    when(reportRepository.findAll()).thenReturn(reports);

    // Act
    List<Report> result = reportQueryService.handle(new GetAllReportsQuery());

    // Assert
    assertEquals(reports, result);
  }

  @Test
  @DisplayName("Given a missing id, when handling GetReportByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenReportIdMissing() {
    // Arrange
    when(reportRepository.findById(5L)).thenReturn(Optional.empty());

    // Act
    Optional<Report> result = reportQueryService.handle(new GetReportByIdQuery(5L));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given reports for an operation, when handling GetReportsByServiceOperationIdQuery, then it returns them")
  void handle_ShouldReturnReportsByOperation_WhenOperationHasReports() {
    // Arrange
    List<Report> reports = List.of(mock(Report.class));
    when(reportRepository.findByServiceOperationId(10L)).thenReturn(reports);

    // Act
    List<Report> result =
        reportQueryService.handle(new GetReportsByServiceOperationIdQuery(10L));

    // Assert
    assertEquals(reports, result);
  }
}
