package com.hampcoders.electrolink.monitoring.application.internal.eventhandlers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.interfaces.acl.InventoryContextFacade;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentStockResource;
import com.hampcoders.electrolink.monitoring.domain.model.events.ServiceCompletedEvent;
import com.hampcoders.electrolink.sdp.interfaces.acl.SdpContextFacade;
import com.hampcoders.electrolink.sdp.interfaces.acl.SdpContextFacade.ServiceComponentRequirement;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockDeductionOnServiceCompletedHandlerTest {

  @Mock
  private SdpContextFacade sdpContextFacade;
  @Mock
  private InventoryContextFacade inventoryContextFacade;

  @InjectMocks
  private StockDeductionOnServiceCompletedHandler handler;

  private static ServiceCompletedEvent event() {
    return new ServiceCompletedEvent(1L, 2L, 3L);
  }

  @Test
  @DisplayName("Given required components in stock, when handling service completed, then it deducts the quantity")
  void handle_ShouldDeductStock_WhenComponentsRequired() {
    // Arrange
    when(sdpContextFacade.fetchRequestServiceId(2L)).thenReturn(Optional.of("100"));
    when(sdpContextFacade.fetchServiceComponentRequirements(100L))
        .thenReturn(List.of(new ServiceComponentRequirement(10L, 2)));
    ComponentStockResource stock =
        new ComponentStockResource(UUID.randomUUID(), 10L, "Resistor", 5, 1, new Date());
    when(inventoryContextFacade.findComponentStock(3L, 10L)).thenReturn(Optional.of(stock));

    // Act
    handler.onServiceCompleted(event());

    // Assert
    verify(inventoryContextFacade).updateComponentStock(3L, 10L, 3);
  }

  @Test
  @DisplayName("Given the request is not found, when handling service completed, then it skips deduction")
  void handle_ShouldSkipDeduction_WhenRequestNotFound() {
    // Arrange
    when(sdpContextFacade.fetchRequestServiceId(2L)).thenReturn(Optional.empty());

    // Act
    handler.onServiceCompleted(event());

    // Assert
    verifyNoInteractions(inventoryContextFacade);
  }

  @Test
  @DisplayName("Given no required components, when handling service completed, then it skips deduction")
  void handle_ShouldSkipDeduction_WhenNoComponentsRequired() {
    // Arrange
    when(sdpContextFacade.fetchRequestServiceId(2L)).thenReturn(Optional.of("100"));
    when(sdpContextFacade.fetchServiceComponentRequirements(100L)).thenReturn(List.of());

    // Act
    handler.onServiceCompleted(event());

    // Assert
    verifyNoInteractions(inventoryContextFacade);
  }
}
