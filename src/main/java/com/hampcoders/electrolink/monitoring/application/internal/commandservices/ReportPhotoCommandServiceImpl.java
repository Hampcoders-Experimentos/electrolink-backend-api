package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import com.hampcoders.electrolink.monitoring.application.internal.outboundservices.PhotoStorageService;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddPhotoCommand;
import com.hampcoders.electrolink.monitoring.domain.model.entities.ReportPhoto;
import com.hampcoders.electrolink.monitoring.domain.services.ReportPhotoCommandService;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportPhotoRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ReportPhotoCommandServiceImpl implements ReportPhotoCommandService {

    private final ReportRepository reportRepository;
    private final ReportPhotoRepository reportPhotoRepository;
    private final PhotoStorageService photoStorageService;

    public ReportPhotoCommandServiceImpl(ReportRepository reportRepository,
                                         ReportPhotoRepository reportPhotoRepository,
                                         PhotoStorageService photoStorageService) {
        this.reportRepository = reportRepository;
        this.reportPhotoRepository = reportPhotoRepository;
        this.photoStorageService = photoStorageService;
    }

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
