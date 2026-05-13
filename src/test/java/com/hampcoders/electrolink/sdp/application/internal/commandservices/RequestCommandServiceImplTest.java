package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import com.hampcoders.electrolink.sdp.application.internal.services.TechnicianMatchingService;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.entities.Bill;
import com.hampcoders.electrolink.sdp.domain.model.entities.Photo;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
import com.hampcoders.electrolink.subscription.interfaces.acl.SubscriptionContextFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestCommandServiceImplTest {

  @Mock
  private RequestRepository requestRepository;

  @Mock
  private SubscriptionContextFacade subscriptionContextFacade;

  @Mock
  private TechnicianMatchingService technicianMatchingService;

  @InjectMocks
  private RequestCommandServiceImpl requestCommandService;

  @Test
  @DisplayName("handle(CreateRequestCommand) should create and save a new request successfully (AAA)")
  void handle_CreateRequest_ShouldSaveAndReturnRequest() {
    var bill = new Bill("2024-10", 150.5, 75.25, "https://example.com/bill.jpg");
    var photos = List.of(new Photo("PHOTO-001", "https://example.com/photo1.jpg"));
    var command = new CreateRequestCommand(
        "123", "TECH-001", "PROP-456", "SVC-789",
        "Air conditioner not cooling properly",
        LocalDate.of(2024, 10, 15), bill, photos, false);

    when(subscriptionContextFacade.canUserMakeRequest(123L)).thenReturn(true);
    when(requestRepository.save(any(Request.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = requestCommandService.handle(command);

    assertNotNull(result);
    assertEquals("123", result.getClientId());
    assertEquals("TECH-001", result.getTechnicianId());
    assertEquals("PROP-456", result.getPropertyId());
    assertEquals("SVC-789", result.getServiceId());
    assertEquals("Air conditioner not cooling properly", result.getProblemDescription());
    assertEquals(LocalDate.of(2024, 10, 15), result.getScheduledDate());
    assertNotNull(result.getBill());
    assertEquals(1, result.getPhotos().size());

    verify(subscriptionContextFacade).canUserMakeRequest(123L);
    verify(requestRepository, times(1)).save(any(Request.class));
    verify(subscriptionContextFacade).recordRequest(123L);
    verifyNoMoreInteractions(requestRepository);
  }

  @Test
  @DisplayName("handle(CreateRequestCommand) should create request without photos (AAA)")
  void handle_CreateRequest_WithoutPhotos_ShouldCreateSuccessfully() {
    var bill = new Bill("2024-11", 200.0, 100.0, "https://example.com/bill2.jpg");
    var command = new CreateRequestCommand(
            "999", "TECH-002", "PROP-111", "SVC-222",
            "Electrical wiring issue", LocalDate.of(2024, 11, 1), bill, null, false);

    when(subscriptionContextFacade.canUserMakeRequest(999L)).thenReturn(true);
    when(requestRepository.save(any(Request.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    var result = requestCommandService.handle(command);

    assertNotNull(result);
    assertEquals("999", result.getClientId());
    var photos = result.getPhotos();
    assertTrue(photos == null || photos.isEmpty());

    verify(subscriptionContextFacade).canUserMakeRequest(999L);
    verify(requestRepository, times(1)).save(any(Request.class));
    verify(subscriptionContextFacade).recordRequest(999L);
  }

  @Test
  @DisplayName("handle(CreateRequestCommand) should throw when request limit reached (AAA)")
  void handle_CreateRequest_WhenLimitReached_ShouldThrowException() {
    var bill = new Bill("2024-10", 100.0, 50.0, "https://example.com/bill.jpg");
    var command = new CreateRequestCommand(
        "123", null, "PROP-456", "SVC-789",
        "Test", LocalDate.of(2024, 10, 15), bill, null, false);

    when(subscriptionContextFacade.canUserMakeRequest(123L)).thenReturn(false);

    assertThrows(IllegalStateException.class, () -> requestCommandService.handle(command));
    verify(requestRepository, never()).save(any());
  }

  @Test
  @DisplayName("handle(UpdateRequestCommand) should update existing request successfully (AAA)")
  void handle_UpdateRequest_WhenRequestExists_ShouldUpdateSuccessfully() {
    Long requestId = 1L;
    var bill = new Bill("2024-10", 150.0, 75.0, "https://example.com/new-bill.jpg");
    var photos = List.of(new Photo("PHOTO-999", "https://example.com/updated-photo.jpg"));
    var command = new UpdateRequestCommand(
        requestId, "CLIENT-999", "TECH-999", "PROP-999", "SVC-999",
        "Updated problem description", LocalDate.of(2024, 10, 20), bill, photos, false);

    var existingRequest = new Request(new CreateRequestCommand(
        "CLIENT-100", "TECH-100", "PROP-100", "SVC-100",
        "Old problem", LocalDate.of(2024, 9, 1),
        new Bill("2024-09", 100.0, 50.0, "https://example.com/old-bill.jpg"), null, false));

    when(requestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
    when(requestRepository.save(any(Request.class))).thenReturn(existingRequest);

    var result = requestCommandService.handle(command);

    assertNotNull(result);
    assertEquals("CLIENT-999", existingRequest.getClientId());
    assertEquals("TECH-999", existingRequest.getTechnicianId());
    assertEquals("Updated problem description", existingRequest.getProblemDescription());

    verify(requestRepository, times(1)).findById(requestId);
    verify(requestRepository, times(1)).save(existingRequest);
  }

  @Test
  @DisplayName("handle(UpdateRequestCommand) should throw exception when request does not exist (AAA)")
  void handle_UpdateRequest_WhenRequestDoesNotExist_ShouldThrowException() {
    Long requestId = 999L;
    var command = new UpdateRequestCommand(
        requestId, "CLIENT-001", "TECH-001", "PROP-001", "SVC-001",
        "Problem description", LocalDate.of(2024, 10, 15),
        new Bill("2024-10", 150.0, 75.0, "https://example.com/bill.jpg"), null, false);

    when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

    var exception = assertThrows(IllegalArgumentException.class,
        () -> requestCommandService.handle(command));
    assertEquals("Request not found", exception.getMessage());
    verify(requestRepository, never()).save(any());
  }

  @Test
  @DisplayName("handle(DeleteRequestCommand) should delete existing request successfully (AAA)")
  void handle_DeleteRequest_WhenRequestExists_ShouldDeleteSuccessfully() {
    Long requestId = 1L;
    var command = new DeleteRequestCommand(requestId);
    var existingRequest = mock(Request.class);
    when(requestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
    doNothing().when(requestRepository).delete(existingRequest);

    requestCommandService.handle(command);

    verify(requestRepository, times(1)).findById(requestId);
    verify(requestRepository, times(1)).delete(existingRequest);
  }

  @Test
  @DisplayName("handle(DeleteRequestCommand) should throw exception when request does not exist (AAA)")
  void handle_DeleteRequest_WhenRequestDoesNotExist_ShouldThrowException() {
    Long requestId = 999L;
    var command = new DeleteRequestCommand(requestId);
    when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

    var exception = assertThrows(IllegalArgumentException.class,
        () -> requestCommandService.handle(command));
    assertEquals("Request not found", exception.getMessage());
    verify(requestRepository, never()).delete(any());
  }
}
