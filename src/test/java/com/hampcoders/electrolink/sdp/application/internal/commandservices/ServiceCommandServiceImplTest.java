package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.entities.ComponentQuantity;
import com.hampcoders.electrolink.sdp.domain.model.entities.Tag;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Policy;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Restriction;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCommandServiceImplTest {

  @Mock
  private ServiceRepository serviceRepository;

  @InjectMocks
  private ServiceCommandServiceImpl serviceCommandServiceImpl;

  private Restriction restriction;
  private Policy policy;
  private List<Tag> tags;
  private List<ComponentQuantity> components;
  private final Long MOCK_SERVICE_ID = 1L;

  @BeforeEach
  void setUp() {
    restriction = new Restriction();
    policy = new Policy();
    tags = new ArrayList<>(List.of(new Tag("Tag1"), new Tag("Tag2")));
    components = new ArrayList<>(List.of(new ComponentQuantity()));
  }

  @Test
  @DisplayName("handle(CreateServiceCommand) should create and return the service ID (AAA)")
  void testHandleCreateServiceCommand_Success() {
    var command = new CreateServiceCommand(
        "Service Name", "Service Description", 100.0, "60h", "Category",
        true, "Admin", policy, restriction, tags, components);

    var savedServiceMock = mock(ServiceEntity.class);
    when(savedServiceMock.getId()).thenReturn(MOCK_SERVICE_ID);
    when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(savedServiceMock);

    Long serviceId = serviceCommandServiceImpl.handle(command);

    assertNotNull(serviceId);
    assertEquals(MOCK_SERVICE_ID, serviceId);
    verify(serviceRepository, times(1)).save(any(ServiceEntity.class));
  }

  @Test
  @DisplayName("handle(UpdateServiceCommand) should update the existing service (AAA)")
  void testHandleUpdateServiceCommand_Success() {
    var command = new UpdateServiceCommand(
        MOCK_SERVICE_ID, "Updated Name", "Updated Description", 150.0,
        "90h", "Updated Category", false, "Admin", policy, restriction, tags, components);

    var existingService = spy(new ServiceEntity(new CreateServiceCommand(
        "Old Name", "Old Description", 100.0, "60h", "Category",
        true, "Admin", policy, restriction, tags, components)));

    when(serviceRepository.findById(MOCK_SERVICE_ID)).thenReturn(Optional.of(existingService));
    when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(existingService);

    serviceCommandServiceImpl.handle(command);

    verify(serviceRepository, times(1)).findById(MOCK_SERVICE_ID);
    verify(existingService, times(1)).updateFrom(command);
    verify(serviceRepository, times(1)).save(existingService);
    assertEquals(command.name(), existingService.getName());
    assertEquals(command.description(), existingService.getDescription());
  }

  @Test
  @DisplayName("handle(UpdateServiceCommand) should throw exception if service not found (AAA)")
  void testHandleUpdateServiceCommand_ServiceNotFound() {
    var command = new UpdateServiceCommand(
        MOCK_SERVICE_ID, "Name", "Desc", 100.0,
        "60h", "Category", true, "Admin", policy, restriction, tags, components);

    when(serviceRepository.findById(MOCK_SERVICE_ID)).thenReturn(Optional.empty());

    var exception = assertThrows(IllegalArgumentException.class,
        () -> serviceCommandServiceImpl.handle(command));
    assertEquals("Service not found with id: " + MOCK_SERVICE_ID, exception.getMessage());
    verify(serviceRepository, never()).save(any(ServiceEntity.class));
  }

  @Test
  @DisplayName("handle(DeleteServiceCommand) should delete existing service (AAA)")
  void testHandleDeleteServiceCommand_Success() {
    var command = new DeleteServiceCommand(MOCK_SERVICE_ID);

    when(serviceRepository.existsById(MOCK_SERVICE_ID)).thenReturn(true);
    doNothing().when(serviceRepository).deleteById(MOCK_SERVICE_ID);

    serviceCommandServiceImpl.handle(command);

    verify(serviceRepository, times(1)).existsById(MOCK_SERVICE_ID);
    verify(serviceRepository, times(1)).deleteById(MOCK_SERVICE_ID);
  }

  @Test
  @DisplayName("handle(DeleteServiceCommand) should throw exception if service not found (AAA)")
  void testHandleDeleteServiceCommand_ServiceNotFound() {
    var command = new DeleteServiceCommand(MOCK_SERVICE_ID);

    when(serviceRepository.existsById(MOCK_SERVICE_ID)).thenReturn(false);

    var exception = assertThrows(IllegalArgumentException.class,
        () -> serviceCommandServiceImpl.handle(command));
    assertEquals("Service not found with id: " + MOCK_SERVICE_ID, exception.getMessage());
    verify(serviceRepository, never()).deleteById(MOCK_SERVICE_ID);
  }
}
