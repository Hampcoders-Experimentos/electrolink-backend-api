package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.ServiceOperationResource;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServiceOperationResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a service operation, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenOperationProvided() {
    // Arrange
    OffsetDateTime startedAt = OffsetDateTime.now();
    OffsetDateTime completedAt = startedAt.plusHours(1);
    ServiceOperation operation = mock(ServiceOperation.class);
    when(operation.getId()).thenReturn(3L);
    when(operation.getRequestId()).thenReturn(new RequestId(1L));
    when(operation.getTechnicianId()).thenReturn(new TechnicianId(2L));
    when(operation.getStartedAt()).thenReturn(startedAt);
    when(operation.getCompletedAt()).thenReturn(completedAt);
    when(operation.getCurrentStatus()).thenReturn(ServiceStatus.COMPLETED);

    // Act
    ServiceOperationResource resource =
        ServiceOperationResourceFromEntityAssembler.toResourceFromEntity(operation);

    // Assert
    assertEquals(3L, resource.id());
    assertEquals(1L, resource.requestId());
    assertEquals(2L, resource.technicianId());
    assertEquals(startedAt, resource.startedAt());
    assertEquals(completedAt, resource.completedAt());
    assertEquals("COMPLETED", resource.currentStatus());
  }

  @Test
  @DisplayName("Given an in-progress operation, when assembling, then it maps the in-progress status")
  void handle_ShouldMapInProgressStatus_WhenOperationIsInProgress() {
    // Arrange
    ServiceOperation operation = mock(ServiceOperation.class);
    when(operation.getId()).thenReturn(4L);
    when(operation.getRequestId()).thenReturn(new RequestId(5L));
    when(operation.getTechnicianId()).thenReturn(new TechnicianId(6L));
    when(operation.getStartedAt()).thenReturn(OffsetDateTime.now());
    when(operation.getCompletedAt()).thenReturn(null);
    when(operation.getCurrentStatus()).thenReturn(ServiceStatus.IN_PROGRESS);

    // Act
    ServiceOperationResource resource =
        ServiceOperationResourceFromEntityAssembler.toResourceFromEntity(operation);

    // Assert
    assertEquals("IN_PROGRESS", resource.currentStatus());
  }

  @Test
  @DisplayName("Given a null operation, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenOperationIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> ServiceOperationResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
