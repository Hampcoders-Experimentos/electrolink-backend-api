package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import com.hampcoders.electrolink.monitoring.application.internal.outboundservices.PhotoStorageService;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddPhotoCommand;
import com.hampcoders.electrolink.monitoring.domain.model.entities.ReportPhoto;
import com.hampcoders.electrolink.monitoring.domain.services.ReportPhotoCommandService;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportPhotoRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the ReportPhotoCommandService that handles adding photos to reports.
 */
@Service
public class ReportPhotoCommandServiceImpl implements ReportPhotoCommandService {

  private final ReportRepository reportRepository;
  private final ReportPhotoRepository reportPhotoRepository;
  private final PhotoStorageService photoStorageService;

  /**
   * Constructor for ReportPhotoCommandServiceImpl.
   *
   * @param reportRepository The repository for accessing Report entities.
   * @param reportPhotoRepository the repository for accessing report photo entities.
   * @param photoStorageService The service for storing photos.
   */
  public ReportPhotoCommandServiceImpl(ReportRepository reportRepository,
                                       ReportPhotoRepository reportPhotoRepository,
                                       PhotoStorageService photoStorageService) {
    this.reportRepository = reportRepository;
    this.reportPhotoRepository = reportPhotoRepository;
    this.photoStorageService = photoStorageService;
  }

  /**
   * Handles the AddPhotoCommand to add a photo to a report.
   * It first checks if the report exists, then stores the photo using the PhotoStorageService,
   * and finally saves the photo details in the ReportPhotoRepository.
   *
   * @param command The command containing the photo details.
   * @return The ID of the newly added photo.
   */
  @Transactional
  @Override
  public Long handle(AddPhotoCommand command) {
    reportRepository.findById(command.reportId())
        .orElseThrow(() -> new IllegalArgumentException(
            "Report not found with ID: " + command.reportId()));

    String storedUrl = photoStorageService.storePhoto(
        command.photoData(),
        command.fileName(),
        command.contentType());

    ReportPhoto photo = new ReportPhoto(command.reportId(), storedUrl);
    reportPhotoRepository.save(photo);
    return photo.getId();
  }
}
