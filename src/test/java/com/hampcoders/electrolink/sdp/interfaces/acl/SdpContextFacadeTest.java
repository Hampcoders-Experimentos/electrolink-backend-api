package com.hampcoders.electrolink.sdp.interfaces.acl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.entities.ComponentQuantity;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestsByClientIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindServiceByIdQuery;
import com.hampcoders.electrolink.sdp.domain.services.RequestCommandService;
import com.hampcoders.electrolink.sdp.domain.services.RequestQueryService;
import com.hampcoders.electrolink.sdp.domain.services.ServiceQueryService;
import com.hampcoders.electrolink.sdp.interfaces.acl.SdpContextFacade.RequestSummary;
import com.hampcoders.electrolink.sdp.interfaces.acl.SdpContextFacade.ServiceComponentRequirement;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.BillResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateRequestResource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SdpContextFacadeTest {

  @Mock
  private RequestCommandService requestCommandService;
  @Mock
  private RequestQueryService requestQueryService;
  @Mock
  private ServiceQueryService serviceQueryService;

  @InjectMocks
  private SdpContextFacade sdpContextFacade;

  private static CreateRequestResource requestResource() {
    return new CreateRequestResource(
        "1", "99", "prop-1", "svc-1", "desc", LocalDate.now(),
        new BillResource("2026-01", 100.0, 50.0, "http://bill"), List.of(), false);
  }

  @Test
  @DisplayName("Given an existing request, when fetching the service id, then it returns the service id")
  void handle_ShouldReturnServiceId_WhenRequestExists() {
    // Arrange
    Request request = mock(Request.class);
    when(request.getServiceId()).thenReturn("svc-1");
    when(requestQueryService.handle(any(FindRequestByIdQuery.class)))
        .thenReturn(Optional.of(request));

    // Act
    Optional<String> result = sdpContextFacade.fetchRequestServiceId(2L);

    // Assert
    assertEquals(Optional.of("svc-1"), result);
  }

  @Test
  @DisplayName("Given a missing request, when fetching the service id, then it returns empty")
  void handle_ShouldReturnEmpty_WhenRequestNotFound() {
    // Arrange
    when(requestQueryService.handle(any(FindRequestByIdQuery.class)))
        .thenReturn(Optional.empty());

    // Act
    Optional<String> result = sdpContextFacade.fetchRequestServiceId(2L);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a missing service, when fetching component requirements, then it returns an empty list")
  void handle_ShouldReturnEmptyRequirements_WhenServiceNotFound() {
    // Arrange
    when(serviceQueryService.handle(any(FindServiceByIdQuery.class)))
        .thenReturn(Optional.<ServiceEntity>empty());

    // Act
    List<ServiceComponentRequirement> result =
        sdpContextFacade.fetchServiceComponentRequirements(100L);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a service with components, when fetching requirements, then it maps them")
  void handle_ShouldMapRequirements_WhenServiceHasComponents() {
    // Arrange
    ComponentQuantity cq = mock(ComponentQuantity.class);
    when(cq.getComponentId()).thenReturn("10");
    when(cq.getQuantity()).thenReturn(2);
    ServiceEntity service = mock(ServiceEntity.class);
    when(service.getComponents()).thenReturn(List.of(cq));
    when(serviceQueryService.handle(any(FindServiceByIdQuery.class)))
        .thenReturn(Optional.of(service));

    // Act
    List<ServiceComponentRequirement> result =
        sdpContextFacade.fetchServiceComponentRequirements(100L);

    // Assert
    assertEquals(1, result.size());
    assertEquals(10L, result.get(0).componentId());
    assertEquals(2, result.get(0).quantity());
  }

  @Test
  @DisplayName("Given a service without components, when fetching requirements, then it returns an empty list")
  void handle_ShouldReturnEmptyRequirements_WhenServiceHasNoComponents() {
    // Arrange
    ServiceEntity service = mock(ServiceEntity.class);
    when(service.getComponents()).thenReturn(List.of());
    when(serviceQueryService.handle(any(FindServiceByIdQuery.class)))
        .thenReturn(Optional.of(service));

    // Act
    List<ServiceComponentRequirement> result =
        sdpContextFacade.fetchServiceComponentRequirements(100L);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a client with requests, when fetching summaries, then it maps them")
  void handle_ShouldMapSummaries_WhenClientHasRequests() {
    // Arrange
    Request request = mock(Request.class);
    when(request.getId()).thenReturn(1L);
    when(request.getServiceId()).thenReturn("svc-1");
    when(request.getScheduledDate()).thenReturn(LocalDate.now());
    when(requestQueryService.handle(any(FindRequestsByClientIdQuery.class)))
        .thenReturn(List.of(request));

    // Act
    List<RequestSummary> result = sdpContextFacade.fetchRequestsByClientId("1");

    // Assert
    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).requestId());
    assertEquals("svc-1", result.get(0).serviceId());
  }

  @Test
  @DisplayName("Given a resource, when creating a request, then it returns the created id")
  void handle_ShouldReturnId_WhenCreatingRequest() {
    // Arrange
    Request created = mock(Request.class);
    when(created.getId()).thenReturn(5L);
    when(requestCommandService.handle(any(CreateRequestCommand.class))).thenReturn(created);

    // Act
    Long result = sdpContextFacade.createRequest(requestResource());

    // Assert
    assertEquals(5L, result);
  }

  @Test
  @DisplayName("Given a resource and id, when updating a request, then it returns the updated id")
  void handle_ShouldReturnId_WhenUpdatingRequest() {
    // Arrange
    Request updated = mock(Request.class);
    when(updated.getId()).thenReturn(7L);
    when(requestCommandService.handle(any(UpdateRequestCommand.class))).thenReturn(updated);

    // Act
    Long result = sdpContextFacade.updateRequest(7L, requestResource());

    // Assert
    assertEquals(7L, result);
  }

  @Test
  @DisplayName("Given a request id, when deleting a request, then it delegates a delete command")
  void handle_ShouldDelegateDelete_WhenDeletingRequest() {
    // Act
    sdpContextFacade.deleteRequest(7L);

    // Assert
    verify(requestCommandService).handle(any(DeleteRequestCommand.class));
  }
}
