package com.hampcoders.electrolink.monitoring.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetAllServiceOperationsQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetServiceOperationByIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetServiceOperationsByTechnicianIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceOperationQueryServiceImplTest {

  @Mock
  private ServiceOperationRepository serviceOperationRepository;

  @InjectMocks
  private ServiceOperationQueryServiceImpl serviceOperationQueryService;

  @Test
  @DisplayName("Given existing operations, when handling GetAllServiceOperationsQuery, then it returns all of them")
  void handle_ShouldReturnAllOperations_WhenQueryingAll() {
    // Arrange
    List<ServiceOperation> operations = List.of(mock(ServiceOperation.class));
    when(serviceOperationRepository.findAll()).thenReturn(operations);

    // Act
    List<ServiceOperation> result =
        serviceOperationQueryService.handle(new GetAllServiceOperationsQuery());

    // Assert
    assertEquals(operations, result);
  }

  @Test
  @DisplayName("Given a missing id, when handling GetServiceOperationByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenOperationIdMissing() {
    // Arrange
    when(serviceOperationRepository.findById(5L)).thenReturn(Optional.empty());

    // Act
    Optional<ServiceOperation> result =
        serviceOperationQueryService.handle(new GetServiceOperationByIdQuery(5L));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a technician with operations, when handling GetServiceOperationsByTechnicianIdQuery, then it returns them")
  void handle_ShouldReturnOperationsByTechnician_WhenTechnicianHasOperations() {
    // Arrange
    List<ServiceOperation> operations = List.of(mock(ServiceOperation.class));
    when(serviceOperationRepository.findByTechnicianId(new TechnicianId(7L)))
        .thenReturn(operations);

    // Act
    List<ServiceOperation> result =
        serviceOperationQueryService.handle(new GetServiceOperationsByTechnicianIdQuery(7L));

    // Assert
    assertEquals(operations, result);
  }
}
