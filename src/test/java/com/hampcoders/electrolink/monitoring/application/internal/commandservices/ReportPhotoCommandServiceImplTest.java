package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.application.internal.outboundservices.PhotoStorageService;
import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Report;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddPhotoCommand;
import com.hampcoders.electrolink.monitoring.domain.model.entities.ReportPhoto;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportPhotoRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportRepository;
import com.hampcoders.electrolink.shared.test.util.ReflectionTestUtils;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportPhotoCommandServiceImplTest {

  @Mock
  private ReportRepository reportRepository;
  @Mock
  private ReportPhotoRepository reportPhotoRepository;
  @Mock
  private PhotoStorageService photoStorageService;

  @InjectMocks
  private ReportPhotoCommandServiceImpl reportPhotoCommandService;

  private static AddPhotoCommand command() {
    return new AddPhotoCommand(1L, new byte[] {1, 2, 3}, "photo.png", "image/png");
  }

  @Test
  @DisplayName("Given an existing report, when handling AddPhotoCommand, then it returns the photo id")
  void handle_ShouldReturnPhotoId_WhenReportExists() {
    // Arrange
    when(reportRepository.findById(1L)).thenReturn(Optional.of(mock(Report.class)));
    when(photoStorageService.storePhoto(any(), any(), any())).thenReturn("http://url/photo.png");
    when(reportPhotoRepository.save(any(ReportPhoto.class))).thenAnswer(invocation -> {
      ReportPhoto photo = invocation.getArgument(0);
      ReflectionTestUtils.setField(photo, "id", 9L);
      return photo;
    });

    // Act
    Long result = reportPhotoCommandService.handle(command());

    // Assert
    assertEquals(9L, result);
  }

  @Test
  @DisplayName("Given a missing report, when handling AddPhotoCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenReportNotFound() {
    // Arrange
    when(reportRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> reportPhotoCommandService.handle(command()));
  }

  @Test
  @DisplayName("Given an existing report, when handling AddPhotoCommand, then it stores the photo data")
  void handle_ShouldStorePhoto_WhenReportExists() {
    // Arrange
    AddPhotoCommand command = command();
    when(reportRepository.findById(1L)).thenReturn(Optional.of(mock(Report.class)));
    when(photoStorageService.storePhoto(any(), any(), any())).thenReturn("http://url/photo.png");
    when(reportPhotoRepository.save(any(ReportPhoto.class))).thenAnswer(invocation -> {
      ReportPhoto photo = invocation.getArgument(0);
      ReflectionTestUtils.setField(photo, "id", 9L);
      return photo;
    });

    // Act
    reportPhotoCommandService.handle(command);

    // Assert
    verify(photoStorageService).storePhoto(command.photoData(), "photo.png", "image/png");
  }
}
