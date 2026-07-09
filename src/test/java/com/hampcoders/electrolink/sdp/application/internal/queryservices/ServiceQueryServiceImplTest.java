package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindServiceByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.GetAllServicesQuery;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ServiceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceQueryServiceImplTest {

  @Mock
  private ServiceRepository serviceRepository;

  @InjectMocks
  private ServiceQueryServiceImpl serviceQueryService;

  @Test
  @DisplayName("Given an existing id, when handling FindServiceByIdQuery, then it returns the service")
  void handle_ShouldReturnService_WhenIdExists() {
    // Arrange
    ServiceEntity service = mock(ServiceEntity.class);
    when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

    // Act
    Optional<ServiceEntity> result = serviceQueryService.handle(new FindServiceByIdQuery(1L));

    // Assert
    assertTrue(result.isPresent());
    assertSame(service, result.get());
  }

  @Test
  @DisplayName("Given a missing id, when handling FindServiceByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenIdMissing() {
    // Arrange
    when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

    // Act
    Optional<ServiceEntity> result = serviceQueryService.handle(new FindServiceByIdQuery(1L));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given existing services, when handling GetAllServicesQuery, then it returns all of them")
  void handle_ShouldReturnAllServices_WhenQueryingAll() {
    // Arrange
    List<ServiceEntity> services = List.of(mock(ServiceEntity.class));
    when(serviceRepository.findAll()).thenReturn(services);

    // Act
    List<ServiceEntity> result = serviceQueryService.handle(new GetAllServicesQuery());

    // Assert
    assertEquals(services, result);
  }
}
