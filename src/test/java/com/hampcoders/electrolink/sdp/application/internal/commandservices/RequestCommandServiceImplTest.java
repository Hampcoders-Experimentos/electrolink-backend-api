package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.application.internal.services.TechnicianMatchingService;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.entities.Bill;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
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
class RequestCommandServiceImplTest {

  @Mock
  private RequestRepository requestRepository;
  @Mock
  private TechnicianMatchingService technicianMatchingService;

  @InjectMocks
  private RequestCommandServiceImpl requestCommandService;

  private static CreateRequestCommand command(String technicianId) {
    Bill bill = new Bill("2026-01", 100.0, 50.0, "http://bill");
    return new CreateRequestCommand("1", technicianId, "prop-1", "svc-1",
        "desc", LocalDate.now(), bill, List.of(), false);
  }

  @Test
  @DisplayName("Given a technician is already assigned, when handling CreateRequestCommand, then it saves the request")
  void handle_ShouldSaveRequest_WhenTechnicianAssigned() {
    // Arrange
    Request saved = mock(Request.class);
    when(requestRepository.save(any(Request.class))).thenReturn(saved);

    // Act
    Request result = requestCommandService.handle(command("99"));

    // Assert
    assertSame(saved, result);
    verify(requestRepository).save(any(Request.class));
  }

  @Test
  @DisplayName("Given no available technician, when handling CreateRequestCommand, then it throws IllegalState")
  void handle_ShouldThrow_WhenNoTechnicianAvailable() {
    // Arrange
    when(technicianMatchingService.findBestTechnicianForRequest(any(Request.class)))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> requestCommandService.handle(command(null)));
  }
}
