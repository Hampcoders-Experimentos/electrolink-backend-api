package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Report;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.DeleteReportCommand;
import com.hampcoders.electrolink.monitoring.domain.services.ReportCommandService;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the command service for Report entities.
 */
@Service
@Transactional
public class ReportCommandServiceImpl implements ReportCommandService {

  private final ServiceOperationRepository serviceOperationRepository;
  private final ReportRepository reportRepository;

  public ReportCommandServiceImpl(ReportRepository reportRepository,
                                  ServiceOperationRepository serviceOperationRepository) {
    this.reportRepository = reportRepository;
    this.serviceOperationRepository = serviceOperationRepository;
  }

  /**
   * Handles the creation of a new report.
   *
   * @param command The command containing report details.
   * @return The ID of the created report.
   */
  @Override
  public Long handle(AddReportCommand command) {
    serviceOperationRepository.findById(command.serviceOperationId())
        .orElseThrow(() -> new IllegalArgumentException(
            "No ServiceOperation found with id: " + command.serviceOperationId()));

    var report = new Report(command);
    reportRepository.save(report);
    return report.getId();
  }

  /**
   * Handles the deletion of an existing report.
   *
   * @param command The command containing the report ID to delete.
   */
  @Override
  public void handle(DeleteReportCommand command) {
    var report = reportRepository.findById(command.reportId())
        .orElseThrow(() -> new IllegalArgumentException("Report not found"));
    reportRepository.delete(report);
  }

}