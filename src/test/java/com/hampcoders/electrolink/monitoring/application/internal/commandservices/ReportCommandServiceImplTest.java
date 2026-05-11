package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import com.hampcoders.electrolink.monitoring.application.internal.outboundservices.PhotoStorageService;
import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Report;
import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddPhotoCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.DeleteReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.entities.ReportPhoto;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ReportType;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportCommandServiceImplTest {
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private ServiceOperationRepository serviceOperationRepository;
    @Mock
    private EntityManager entityManager;
    @Mock
    private PhotoStorageService photoStorageService;

    @InjectMocks
    private ReportCommandServiceImpl reportCommandService;

    // -------------------------------------------------------------------------
    // handle(AddReportCommand command) - CREATE REPORT
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("handle(AddReportCommand) should save Report when ServiceOperation exists (AAA)")
    void handle_AddReportCommand_ShouldSaveReport_WhenServiceOperationExists(){
        // Arrange
        Long serviceOperationId = 10L;
        when(serviceOperationRepository.findById(eq(serviceOperationId))).thenReturn(Optional.of(mock(ServiceOperation.class)));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new AddReportCommand(serviceOperationId, ReportType.MAINTENANCE, "Initial inspection report");

        // Act
        Long id = reportCommandService.handle(command);

        // Assert
        assertNull(id, "Expected id to be null because repository save is mocked and no id is set");
        verify(serviceOperationRepository, times(1)).findById(serviceOperationId);
        verify(reportRepository, times(1)).save(any(Report.class));
        verifyNoMoreInteractions(serviceOperationRepository, reportRepository);
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("handle(AddReportCommand) should throw IllegalArgumentException if ServiceOperation not found (AAA)")
    void handle_AddReportCommand_ShouldThrowException_WhenServiceOperationNotFound(){
        // Arrange
        Long serviceOperationId = 10L;
        when(serviceOperationRepository.findById(eq(serviceOperationId))).thenReturn(Optional.empty());

        var command = new AddReportCommand(serviceOperationId, ReportType.MAINTENANCE, "Preventive maintenance");

        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class, () -> reportCommandService.handle(command));

        assertFalse(ex.getMessage().contains("No ServiceOperation found with RequestId: "));
        verify(serviceOperationRepository, times(1)).findById(serviceOperationId);
        verifyNoMoreInteractions(serviceOperationRepository);
        verifyNoInteractions(reportRepository, entityManager);
    }

    // -------------------------------------------------------------------------
    // handle(DeleteReportCommand command) - DELETE REPORT
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("handle(DeleteReportCommand) should delete Report when found (AAA)")
    void handle_DeleteReportCommand_ShouldDeleteReport(){
        var reportId = 10L;
        var existingReport = mock(Report.class);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(existingReport));
        var command = new DeleteReportCommand(reportId);

        // Act
        reportCommandService.handle(command);

        // Assert
        verify(reportRepository, times(1)).findById(reportId);
        verify(reportRepository, times(1)).delete(existingReport);
        verifyNoMoreInteractions(reportRepository);
        verifyNoInteractions(serviceOperationRepository, entityManager);
    }

    @Test
    @DisplayName("handle(DeleteReportCommand) should throw IllegalArgumentException if Report not found (AAA)")
    void handle_DeleteReportCommand_ShouldThrowException_WhenNotFound() {
        // Arrange
        var reportId = 10L;

        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());
        var command = new DeleteReportCommand(reportId);

        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class, () -> {
            reportCommandService.handle(command);
        }, "Debe lanzar IllegalArgumentException si no existe Report.");

        assertTrue(ex.getMessage().contains("Report not found"));

        verify(reportRepository, times(1)).findById(reportId);
        verify(reportRepository, never()).delete(any(Report.class));
        verifyNoMoreInteractions(reportRepository);
        verifyNoInteractions(serviceOperationRepository, entityManager);
    }

    // -------------------------------------------------------------------------
    // handle(AddPhotoCommand command) - ADD PHOTO
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("handle(AddPhotoCommand) should persist ReportPhoto and return its ID (AAA)")
    void handle_AddPhotoCommand_ShouldPersistPhotoAndReturnId() {
        // Arrange
        Long reportId = 11L;
        var url = "https://example.com/photo.jpg";

        when(photoStorageService.storePhoto(any(byte[].class), anyString(), anyString())).thenReturn(url);

        var command = new AddPhotoCommand(reportId, new byte[]{1, 2, 3}, "test.jpg", "image/jpeg");

        // Act
        var actualId = reportCommandService.handle(command);

        // Assert
        assertNull(actualId, "Expected id to be null because repository save is mocked and no id is set");

        verify(entityManager, times(1)).persist(any(ReportPhoto.class));
        verifyNoMoreInteractions(entityManager);
        verifyNoInteractions(reportRepository, serviceOperationRepository);
    }
}
