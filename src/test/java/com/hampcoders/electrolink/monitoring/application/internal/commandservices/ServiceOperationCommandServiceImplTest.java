package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.commands.CreateServiceOperationCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.UpdateServiceStatusCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceOperationCommandServiceImplTest {
    @Mock
    private ServiceOperationRepository serviceOperationRepository;

    @InjectMocks
    private ServiceOperationCommandServiceImpl serviceOperationCommandService;

    // -------------------------------------------------------------------------
    // handle(CreateServiceOperationCommand command) - CREATE
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("handle(CreateServiceOperationCommand) should create and save ServiceOperation and return RequestId (AAA)")
    void handle_CreateServiceOperationCommand_ShouldSaveAndReturnRequestId() {
        // Arrange
        RequestId requestId = new RequestId(10L);
        TechnicianId technicianId = new TechnicianId(11L);
        var startedAt = OffsetDateTime.now().minusHours(1);
        var currentStatus = ServiceStatus.PENDING;

        when(serviceOperationRepository.save(any(ServiceOperation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new CreateServiceOperationCommand(
                requestId,
                technicianId,
                startedAt,
                null, // completedAt es null al inicio
                currentStatus
        );

        // Act
        Long id = serviceOperationCommandService.handle(command);

        // Assert
        assertNull(id, "Expected id to be null because repository save is mocked and no id is set");

        verify(serviceOperationRepository).save(any(ServiceOperation.class));
        verifyNoMoreInteractions(serviceOperationRepository);
    }

    // -------------------------------------------------------------------------
    // handle(UpdateServiceStatusCommand command) - UPDATE
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("handle(UpdateServiceStatusCommand) should update status to PENDING/IN_PROGRESS and save (AAA)")
    void handle_UpdateServiceStatusCommand_ShouldUpdateStatusAndSave() {
        // Arrange
        Long serviceOperationId = 10L;
        var newStatus = ServiceStatus.PENDING.toString();
        ServiceOperation serviceOperation = mock(ServiceOperation.class);

        when(serviceOperationRepository.findById(eq(serviceOperationId))).thenReturn(Optional.of(serviceOperation));

        var command = new UpdateServiceStatusCommand(10L, newStatus);

        // Act
        serviceOperationCommandService.handle(command);

        // Assert
        verify(serviceOperationRepository, times(1)).findById(serviceOperationId);
        verify(serviceOperation, times(1)).updateStatus(ServiceStatus.PENDING);
        verify(serviceOperationRepository, times(1)).save(serviceOperation);
        verifyNoMoreInteractions(serviceOperationRepository);
    }

    @Test
    @DisplayName("handle(UpdateServiceStatusCommand) should update status to COMPLETED and save (AAA)")
    void handle_UpdateServiceStatusCommand_ShouldUpdateToCompleted_AndSave() {
        // Arrange
        var serviceOperationId = 10L;
        var newStatus = ServiceStatus.COMPLETED.toString();
        var existingServiceOperation = mock(ServiceOperation.class);

        when(serviceOperationRepository.findById(serviceOperationId)).thenReturn(Optional.of(existingServiceOperation));

        var command = new UpdateServiceStatusCommand(serviceOperationId, newStatus);

        // Act
        serviceOperationCommandService.handle(command);

        // Assert
        verify(serviceOperationRepository).findById(serviceOperationId);
        verify(existingServiceOperation).updateStatus(ServiceStatus.COMPLETED);
        verify(serviceOperationRepository).save(existingServiceOperation);
        verifyNoMoreInteractions(serviceOperationRepository);
    }

    @Test
    @DisplayName("handle(UpdateServiceStatusCommand) should throw IllegalArgumentException if ServiceOperation not found (AAA)")
    void handle_UpdateServiceStatusCommand_ShouldThrowException_WhenNotFound(){
        // Arrange
        var serviceOperationId = 10L;
        var newStatus = ServiceStatus.COMPLETED.toString();

        when(serviceOperationRepository.findById(eq(serviceOperationId))).thenReturn(Optional.empty());

        var command = new UpdateServiceStatusCommand(serviceOperationId, newStatus);

        // Act + Assert
        var ex = assertThrows(IllegalArgumentException.class, () -> {
            serviceOperationCommandService.handle(command);
        }, "Debe lanzar IllegalArgumentException si ServiceOperation no existe.");

        assertTrue(ex.getMessage().contains("ServiceOperation not found"));
        verify(serviceOperationRepository, times(1)).findById(eq(serviceOperationId));
        verify(serviceOperationRepository, never()).save(any(ServiceOperation.class));
        verifyNoMoreInteractions(serviceOperationRepository);
    }
}
