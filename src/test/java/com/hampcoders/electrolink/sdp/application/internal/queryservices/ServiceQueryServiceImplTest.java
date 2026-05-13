package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindServiceByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.GetAllServicesQuery;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ServiceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceQueryServiceImplTest {

  @Mock
  private ServiceRepository serviceRepository;

  @InjectMocks
  private ServiceQueryServiceImpl service;

  @Test
  @DisplayName("handle(FindServiceByIdQuery) should return Optional with Service when it exists (AAA)")
  void handle_FindById_WhenServiceExists_ShouldReturnOptionalWithService() {
    Long serviceId = 100L;
    var query = new FindServiceByIdQuery(serviceId);
    var expected = mock(ServiceEntity.class);
    when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(expected));

    var actual = service.handle(query);

    assertTrue(actual.isPresent());
    assertSame(expected, actual.get());
    verify(serviceRepository, times(1)).findById(serviceId);
  }

  @Test
  @DisplayName("handle(FindServiceByIdQuery) should return empty Optional when Service does not exist (AAA)")
  void handle_FindById_WhenServiceDoesNotExist_ShouldReturnEmptyOptional() {
    Long serviceId = 999L;
    var query = new FindServiceByIdQuery(serviceId);
    when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

    var actual = service.handle(query);

    assertFalse(actual.isPresent());
    verify(serviceRepository, times(1)).findById(serviceId);
  }

  @Test
  @DisplayName("handle(GetAllServicesQuery) should return List with multiple Services when they exist (AAA)")
  void handle_GetAll_WhenMultipleServicesExist_ShouldReturnList() {
    var serviceA = mock(ServiceEntity.class);
    var serviceB = mock(ServiceEntity.class);
    when(serviceRepository.findAll()).thenReturn(List.of(serviceA, serviceB));
    var query = new GetAllServicesQuery();

    var actual = service.handle(query);

    assertNotNull(actual);
    assertEquals(2, actual.size());
    assertSame(serviceA, actual.get(0));
    assertSame(serviceB, actual.get(1));
    verify(serviceRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("handle(GetAllServicesQuery) should return empty List when no Services exist (AAA)")
  void handle_GetAll_WhenNoServicesExist_ShouldReturnEmptyList() {
    when(serviceRepository.findAll()).thenReturn(Collections.emptyList());
    var query = new GetAllServicesQuery();

    var actual = service.handle(query);

    assertNotNull(actual);
    assertTrue(actual.isEmpty());
    verify(serviceRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("handle(GetAllServicesQuery) should return List with single Service when only one exists (AAA)")
  void handle_GetAll_WhenSingleServiceExists_ShouldReturnListWithOne() {
    var serviceEntity = mock(ServiceEntity.class);
    when(serviceRepository.findAll()).thenReturn(List.of(serviceEntity));
    var query = new GetAllServicesQuery();

    var actual = service.handle(query);

    assertNotNull(actual);
    assertEquals(1, actual.size());
    assertSame(serviceEntity, actual.get(0));
    verify(serviceRepository, times(1)).findAll();
  }
}
