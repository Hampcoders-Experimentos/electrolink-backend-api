package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.ServiceOperationResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceOperationResourceFromEntityAssemblerTest {
    @Test
    @DisplayName("toResourceFromEntity should map a COMPLETED ServiceOperation entity to ServiceOperationResource (AAA)")
    void toResourceFromEntity_ShouldMapCompletedEntityToResource() {
        // Arrange
        Long serviceOperationId = 100L;
        Long requestId = 10L;
        Long technicianId = 20L;
        OffsetDateTime startedAt = OffsetDateTime.parse("2025-10-05T09:00:00Z");
        OffsetDateTime completedAt = OffsetDateTime.parse("2025-10-05T12:30:00Z");
        ServiceStatus status = ServiceStatus.COMPLETED;

        var requestIdVo = new RequestId(requestId);
        var technicianIdVo = new TechnicianId(technicianId);

        ServiceOperation entity = mock(ServiceOperation.class);
        when(entity.getId()).thenReturn(serviceOperationId);
        when(entity.getRequestId()).thenReturn(requestIdVo);
        when(entity.getTechnicianId()).thenReturn(technicianIdVo);
        when(entity.getStartedAt()).thenReturn(startedAt);
        when(entity.getCompletedAt()).thenReturn(completedAt);
        when(entity.getCurrentStatus()).thenReturn(status);

        // Act
        ServiceOperationResource resource = ServiceOperationResourceFromEntityAssembler.toResourceFromEntity(entity);

        // Assert
        assertNotNull(resource, "El recurso retornado no debe ser nulo.");

        assertEquals(serviceOperationId, resource.id(), "El ID del servicio debe coincidir.");
        assertEquals(requestId, resource.requestId(), "El RequestId (Long) debe coincidir.");
        assertEquals(technicianId, resource.technicianId(), "El TechnicianId (Long) debe coincidir.");
        assertEquals(startedAt, resource.startedAt(), "La fecha de inicio debe coincidir.");
        assertEquals(completedAt, resource.completedAt(), "La fecha de finalización debe coincidir.");
        assertEquals(status.name(), resource.currentStatus(), "El estado debe convertirse a su nombre String.");

        verify(entity).getId();
        verify(entity).getRequestId();
        verify(entity).getTechnicianId();
        verify(entity).getStartedAt();
        verify(entity).getCompletedAt();
        verify(entity).getCurrentStatus();
        verifyNoMoreInteractions(entity);
    }
}
