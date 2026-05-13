package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestsByClientIdQuery;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
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
class RequestQueryServiceImplTest {

  @Mock
  private RequestRepository requestRepository;

  @InjectMocks
  private RequestQueryServiceImpl service;

  @Test
  @DisplayName("handle(FindRequestByIdQuery) should return Optional with Request when it exists (AAA)")
  void handle_FindById_WhenRequestExists_ShouldReturnOptionalWithRequest() {
    Long requestId = 10L;
    var query = new FindRequestByIdQuery(requestId);
    var expected = mock(Request.class);
    when(requestRepository.findById(requestId)).thenReturn(Optional.of(expected));

    var actual = service.handle(query);

    assertTrue(actual.isPresent());
    assertSame(expected, actual.get());
    verify(requestRepository, times(1)).findById(requestId);
  }

  @Test
  @DisplayName("handle(FindRequestByIdQuery) should return empty Optional when Request does not exist (AAA)")
  void handle_FindById_WhenRequestDoesNotExist_ShouldReturnEmptyOptional() {
    Long requestId = 999L;
    var query = new FindRequestByIdQuery(requestId);
    when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

    var actual = service.handle(query);

    assertFalse(actual.isPresent());
    verify(requestRepository, times(1)).findById(requestId);
  }

  @Test
  @DisplayName("handle(FindRequestsByClientIdQuery) should return List with multiple Requests (AAA)")
  void handle_FindByClientId_WhenMultipleRequestsExist_ShouldReturnList() {
    String clientId = "CLIENT-123";
    var requestA = mock(Request.class);
    var requestB = mock(Request.class);
    when(requestRepository.findByClientId(clientId)).thenReturn(List.of(requestA, requestB));
    var query = new FindRequestsByClientIdQuery(clientId);

    var actual = service.handle(query);

    assertNotNull(actual);
    assertEquals(2, actual.size());
    assertSame(requestA, actual.get(0));
    assertSame(requestB, actual.get(1));
    verify(requestRepository, times(1)).findByClientId(clientId);
  }

  @Test
  @DisplayName("handle(FindRequestsByClientIdQuery) should return empty List when no Requests (AAA)")
  void handle_FindByClientId_WhenNoRequestsExist_ShouldReturnEmptyList() {
    String clientId = "CLIENT-999";
    when(requestRepository.findByClientId(clientId)).thenReturn(Collections.emptyList());
    var query = new FindRequestsByClientIdQuery(clientId);

    var actual = service.handle(query);

    assertNotNull(actual);
    assertTrue(actual.isEmpty());
    verify(requestRepository, times(1)).findByClientId(clientId);
  }

  @Test
  @DisplayName("handle(FindRequestsByClientIdQuery) should return single Request list (AAA)")
  void handle_FindByClientId_WhenSingleRequestExists_ShouldReturnListWithOne() {
    String clientId = "CLIENT-456";
    var request = mock(Request.class);
    when(requestRepository.findByClientId(clientId)).thenReturn(List.of(request));
    var query = new FindRequestsByClientIdQuery(clientId);

    var actual = service.handle(query);

    assertNotNull(actual);
    assertEquals(1, actual.size());
    assertSame(request, actual.get(0));
    verify(requestRepository, times(1)).findByClientId(clientId);
  }
}
