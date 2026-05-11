package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import com.hampcoders.electrolink.monitoring.application.internal.outboundservices.PhotoStorageService;
import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Report;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddPhotoCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.DeleteReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.entities.ReportPhoto;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ReportType;
import com.hampcoders.electrolink.monitoring.domain.services.ReportCommandService;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

/**
 * Implementation of the command service for Report entities.
 */
@Service
public class ReportCommandServiceImpl implements ReportCommandService {

  private final ServiceOperationRepository serviceOperationRepository;
  private final ReportRepository reportRepository;
  private final EntityManager entityManager;
  private final PhotoStorageService photoStorageService;

  /**
   * Constructor for ReportCommandServiceImpl.
   *
   * @param reportRepository The repository for managing Report entities.
   * @param entityManager The JPA EntityManager for managing ReportPhoto persistence.
   * @param serviceOperationRepository The repository for validating ServiceOperation existence.
   * @param photoStorageService The service for storing photo files.
   */
  public ReportCommandServiceImpl(ReportRepository reportRepository, EntityManager entityManager,
                                  ServiceOperationRepository serviceOperationRepository,
                                  PhotoStorageService photoStorageService) {
    this.reportRepository = reportRepository;
    this.entityManager = entityManager;
    this.serviceOperationRepository = serviceOperationRepository;
    this.photoStorageService = photoStorageService;
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

    var report = new Report(
        command.serviceOperationId(),
        ReportType.valueOf(String.valueOf(command.reportType())),
        command.description()
    );
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

  /**
   * Handles the addition of a new photo to a report.
   *
   * @param command The command containing photo details.
   * @return The ID of the created report photo.
   */
  @Override
  public Long handle(AddPhotoCommand command) {
    String storedUrl = photoStorageService.storePhoto(
        command.photoData(),
        command.fileName(),
        command.contentType());
    var reportPhoto = new ReportPhoto(
        command.reportId(),
        storedUrl
    );
    entityManager.persist(reportPhoto);
    return reportPhoto.getId();
  }
}