package com.hampcoders.electrolink.sdpTests.application.internal.commandservicesTests;

import com.hampcoders.electrolink.sdp.application.internal.commandservices.RequestCommandServiceImpl;

import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateRequestResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateRequestResource.BillResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateRequestResource.PhotoResource;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateRequestCommand;

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
public class RequestCommandServiceImplTest {

  @Mock
  private RequestRepository requestRepository;

  @InjectMocks
  private RequestCommandServiceImpl requestCommandService;

  // ========== Tests for CreateRequestCommand ==========

  @Test
  @DisplayName("handle(CreateRequestCommand) should create and save a new request successfully")
  void handle_CreateRequest_ShouldSaveAndReturnRequest() {
    // Arrange
    BillResource billResource = new BillResource(
        "2024-10",
        150.5,
        75.25,
        "https://example.com/bill.jpg"
    );

    PhotoResource photoResource = new PhotoResource("PHOTO-001", "https://example.com/photo1.jpg");

    CreateRequestResource resource = new CreateRequestResource(
        "CLIENT-123",
        "TECH-001",
        "PROP-456",
        "SVC-789",
        "Air conditioner not cooling properly",
        LocalDate.of(2024, 10, 15),
        billResource,
        List.of(photoResource),
        false
    );

    var command = new CreateRequestCommand(resource);

    when(requestRepository.save(any(Request.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Request result = requestCommandService.handle(command);

    // Assert
    assertNotNull(result, "Result should not be null");
    assertEquals("CLIENT-123", result.getClientId(), "Client ID should match");
    assertEquals("TECH-001", result.getTechnicianId(), "Technician ID should match");
    assertEquals("PROP-456", result.getPropertyId(), "Property ID should match");
    assertEquals("SVC-789", result.getServiceId(), "Service ID should match");
    assertEquals("Air conditioner not cooling properly", result.getProblemDescription(),
            "Problem description should match");
    assertEquals(LocalDate.of(2024, 10, 15), result.getScheduledDate(),
            "Scheduled date should match");
    assertNotNull(result.getBill(), "Bill should not be null");
    assertEquals(1, result.getPhotos().size(), "Should have 1 photo");

    verify(requestRepository, times(1)).save(any(Request.class));
    verifyNoMoreInteractions(requestRepository);
  }

  @Test
  @DisplayName("handle(CreateRequestCommand) should create request without photos")
  void handle_CreateRequest_WithoutPhotos_ShouldCreateSuccessfully() {
    // Arrange
    BillResource billResource = new BillResource(
        "2024-11",
        200.0,
        100.0,
        "https://example.com/bill2.jpg"
    );

    CreateRequestResource resource = new CreateRequestResource(
        "CLIENT-999",
        "TECH-002",
        "PROP-111",
        "SVC-222",
        "Electrical wiring issue",
        LocalDate.of(2024, 11, 1),
        billResource,
        null,
        false
    );

    var command = new CreateRequestCommand(resource);

    when(requestRepository.save(any(Request.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Request result = requestCommandService.handle(command);

    // Assert
    assertNotNull(result, "Result should not be null");
    assertEquals("CLIENT-999", result.getClientId(), "Client ID should match");
    assertEquals("TECH-002", result.getTechnicianId(), "Technician ID should match");
    assertTrue(result.getPhotos().isEmpty() || result.getPhotos().size() == 0,
            "Photos should be empty");

    verify(requestRepository, times(1)).save(any(Request.class));
    verifyNoMoreInteractions(requestRepository);
  }

  // ========== Tests for UpdateRequestCommand ==========

  @Test
  @DisplayName("handle(UpdateRequestCommand) should update existing request successfully")
  void handle_UpdateRequest_WhenRequestExists_ShouldUpdateSuccessfully() {
    // Arrange
    Long requestId = 1L;

    BillResource originalBill = new BillResource("2024-09", 100.0,
            50.0, "https://example.com/old-bill.jpg");
    CreateRequestResource originalResource = new CreateRequestResource(
        "CLIENT-100",
        "TECH-100",
        "PROP-100",
        "SVC-100",
        "Old problem",
        LocalDate.of(2024, 9, 1),
        originalBill,
        null,
        false
    );

    Request existingRequest = new Request(
        originalResource.clientId(),
        originalResource.technicianId(),
        originalResource.propertyId(),
        originalResource.serviceId(),
        originalResource.problemDescription(),
        originalResource.scheduledDate(),
        null,
        null
    );

    BillResource updatedBill = new BillResource("2024-10", 150.0,
            75.0, "https://example.com/new-bill.jpg");
    CreateRequestResource updatedResource = new CreateRequestResource(
        "CLIENT-999",
        "TECH-999",
        "PROP-999",
        "SVC-999",
        "Updated problem description",
        LocalDate.of(2024, 10, 20),
        updatedBill,
        List.of(new PhotoResource("PHOTO-999", "https://example.com/updated-photo.jpg")),
        false
    );

    var command = new UpdateRequestCommand(requestId, updatedResource);

    when(requestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
    when(requestRepository.save(any(Request.class))).thenReturn(existingRequest);

    // Act
    Request result = requestCommandService.handle(command);

    // Assert
    assertNotNull(result, "Result should not be null");
    assertEquals("CLIENT-999", existingRequest.getClientId(), "Client ID should be updated");
    assertEquals("TECH-999", existingRequest.getTechnicianId(), "Technician ID should be updated");
    assertEquals("PROP-999", existingRequest.getPropertyId(), "Property ID should be updated");
    assertEquals("SVC-999", existingRequest.getServiceId(), "Service ID should be updated");
    assertEquals("Updated problem description", existingRequest.getProblemDescription(),
            "Problem description should be updated");
    assertEquals(LocalDate.of(2024, 10, 20), existingRequest.getScheduledDate(),
            "Scheduled date should be updated");

    verify(requestRepository, times(1)).findById(requestId);
    verify(requestRepository, times(1)).save(existingRequest);
    verifyNoMoreInteractions(requestRepository);
  }

  @Test
  @DisplayName("handle(UpdateRequestCommand) should throw exception when request does not exist")
  void handle_UpdateRequest_WhenRequestDoesNotExist_ShouldThrowException() {
    // Arrange
    Long requestId = 999L;

    BillResource billResource = new BillResource("2024-10", 150.0,
            75.0, "https://example.com/bill.jpg");
    CreateRequestResource resource = new CreateRequestResource(
        "CLIENT-001",
        "TECH-001",
        "PROP-001",
        "SVC-001",
        "Problem description",
        LocalDate.of(2024, 10, 15),
        billResource,
        null,
        false
    );

    var command = new UpdateRequestCommand(requestId, resource);

    when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> requestCommandService.handle(command),
        "Should throw IllegalArgumentException when request not found"
    );

    assertEquals("Request not found", exception.getMessage(), "Exception message should match");
    verify(requestRepository, times(1)).findById(requestId);
    verify(requestRepository, never()).save(any());
    verifyNoMoreInteractions(requestRepository);
  }

  // ========== Tests for DeleteRequestCommand ==========

  @Test
  @DisplayName("handle(DeleteRequestCommand) should delete existing request successfully")
  void handle_DeleteRequest_WhenRequestExists_ShouldDeleteSuccessfully() {
    // Arrange
    Long requestId = 1L;
    var command = new DeleteRequestCommand(requestId);

    Request existingRequest = mock(Request.class);
    when(requestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
    doNothing().when(requestRepository).delete(existingRequest);

    // Act
    requestCommandService.handle(command);

    // Assert
    verify(requestRepository, times(1)).findById(requestId);
    verify(requestRepository, times(1)).delete(existingRequest);
    verifyNoMoreInteractions(requestRepository);
  }

  @Test
  @DisplayName("handle(DeleteRequestCommand) should throw exception when request does not exist")
  void handle_DeleteRequest_WhenRequestDoesNotExist_ShouldThrowException() {
    // Arrange
    Long requestId = 999L;
    var command = new DeleteRequestCommand(requestId);

    when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> requestCommandService.handle(command),
        "Should throw IllegalArgumentException when request not found"
    );

    assertEquals("Request not found", exception.getMessage(), "Exception message should match");
    verify(requestRepository, times(1)).findById(requestId);
    verify(requestRepository, never()).delete(any());
    verifyNoMoreInteractions(requestRepository);
  }
}
