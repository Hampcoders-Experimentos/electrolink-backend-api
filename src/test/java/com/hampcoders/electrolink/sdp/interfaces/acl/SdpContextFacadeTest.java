package com.hampcoders.electrolink.sdp.interfaces.acl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindRequestByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindServiceByIdQuery;
import com.hampcoders.electrolink.sdp.domain.services.RequestCommandService;
import com.hampcoders.electrolink.sdp.domain.services.RequestQueryService;
import com.hampcoders.electrolink.sdp.domain.services.ServiceQueryService;
import com.hampcoders.electrolink.sdp.interfaces.acl.SdpContextFacade.ServiceComponentRequirement;
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
}
