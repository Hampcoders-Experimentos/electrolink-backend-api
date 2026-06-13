package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestsByClientIdQuery;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestQueryServiceImplTest {

  @Mock
  private RequestRepository requestRepository;

  @InjectMocks
  private RequestQueryServiceImpl requestQueryService;

  @Test
  @DisplayName("Given an existing id, when handling FindRequestByIdQuery, then it returns the request")
  void handle_ShouldReturnRequest_WhenIdExists() {
    // Arrange
    Request request = mock(Request.class);
    when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

    // Act
    Optional<Request> result = requestQueryService.handle(new FindRequestByIdQuery(1L));

    // Assert
    assertTrue(result.isPresent());
    assertSame(request, result.get());
  }

  @Test
  @DisplayName("Given a missing id, when handling FindRequestByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenIdMissing() {
    // Arrange
    when(requestRepository.findById(1L)).thenReturn(Optional.empty());

    // Act
    Optional<Request> result = requestQueryService.handle(new FindRequestByIdQuery(1L));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a client with requests, when handling FindRequestsByClientIdQuery, then it returns them")
  void handle_ShouldReturnRequestsByClient_WhenClientHasRequests() {
    // Arrange
    List<Request> requests = List.of(mock(Request.class));
    when(requestRepository.findByClientId("1")).thenReturn(requests);

    // Act
    List<Request> result = requestQueryService.handle(new FindRequestsByClientIdQuery("1"));

    // Assert
    assertEquals(requests, result);
  }
}
