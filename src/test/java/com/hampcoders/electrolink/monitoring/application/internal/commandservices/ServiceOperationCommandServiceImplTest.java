package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.commands.CreateServiceOperationCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.UpdateServiceStatusCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import com.hampcoders.electrolink.shared.test.util.ReflectionTestUtils;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceOperationCommandServiceImplTest {

  @Mock
  private ServiceOperationRepository serviceOperationRepository;

  @InjectMocks
  private ServiceOperationCommandServiceImpl serviceOperationCommandService;

  @Test
  @DisplayName("Given a valid command, when handling CreateServiceOperationCommand, then it returns the id")
  void handle_ShouldReturnServiceOperationId_WhenCreated() {
    // Arrange
    CreateServiceOperationCommand command = new CreateServiceOperationCommand(
        new RequestId(1L), new TechnicianId(2L), OffsetDateTime.now(), null,
        ServiceStatus.IN_PROGRESS);
    when(serviceOperationRepository.save(any(ServiceOperation.class))).thenAnswer(invocation -> {
      ServiceOperation operation = invocation.getArgument(0);
      ReflectionTestUtils.setField(operation, "id", 3L);
      return operation;
    });

    // Act
    Long result = serviceOperationCommandService.handle(command);

    // Assert
    assertEquals(3L, result);
  }

  @Test
  @DisplayName("Given a missing operation, when handling UpdateServiceStatusCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenUpdatingMissingOperation() {
    // Arrange
    UpdateServiceStatusCommand command = new UpdateServiceStatusCommand(10L, "COMPLETED");
    when(serviceOperationRepository.findById(10L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> serviceOperationCommandService.handle(command));
  }

  @Test
  @DisplayName("Given an existing operation, when handling UpdateServiceStatusCommand, then it updates the status")
  void handle_ShouldUpdateStatus_WhenOperationExists() {
    // Arrange
    UpdateServiceStatusCommand command = new UpdateServiceStatusCommand(10L, "COMPLETED");
    ServiceOperation operation = mock(ServiceOperation.class);
    when(serviceOperationRepository.findById(10L)).thenReturn(Optional.of(operation));

    // Act
    serviceOperationCommandService.handle(command);

    // Assert
    verify(operation).updateStatus(ServiceStatus.COMPLETED);
    verify(serviceOperationRepository).save(operation);
  }
}
