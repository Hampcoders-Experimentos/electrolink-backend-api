package com.hampcoders.electrolink.monitoring.application.internal.eventhandlers;

import com.hampcoders.electrolink.assets.interfaces.acl.InventoryContextFacade;
import com.hampcoders.electrolink.assets.interfaces.rest.resource.ComponentStockResource;
import com.hampcoders.electrolink.monitoring.domain.model.events.ServiceCompletedEvent;
import com.hampcoders.electrolink.sdp.interfaces.acl.SdpContextFacade;
import com.hampcoders.electrolink.sdp.interfaces.acl.SdpContextFacade.ServiceComponentRequirement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockDeductionOnServiceCompletedHandlerTest {

    @Mock
    private SdpContextFacade sdpContextFacade;

    @Mock
    private InventoryContextFacade inventoryContextFacade;

    @InjectMocks
    private StockDeductionOnServiceCompletedHandler handler;

    @Test
    @DisplayName("onServiceCompleted should deduct stock correctly (AAA)")
    void onServiceCompleted_ShouldDeductStock_WhenValid() {
        // Arrange
        Long serviceOpId = 1L;
        Long requestId = 100L;
        Long technicianId = 200L;
        String serviceIdStr = "500";
        Long serviceId = 500L;
        Long componentId = 10L;
        int requiredQty = 3;
        int availableQty = 10;
        
        ServiceCompletedEvent event = new ServiceCompletedEvent(serviceOpId, requestId, technicianId);

        when(sdpContextFacade.fetchRequestServiceId(requestId)).thenReturn(Optional.of(serviceIdStr));
        
        ServiceComponentRequirement requirement = mock(ServiceComponentRequirement.class);
        when(requirement.componentId()).thenReturn(componentId);
        when(requirement.quantity()).thenReturn(requiredQty);
        
        when(sdpContextFacade.fetchServiceComponentRequirements(serviceId))
                .thenReturn(List.of(requirement));

        ComponentStockResource stockResource = mock(ComponentStockResource.class);
        when(stockResource.quantityAvailable()).thenReturn(availableQty);
        when(inventoryContextFacade.findComponentStock(technicianId, componentId))
                .thenReturn(Optional.of(stockResource));

        // Act
        handler.onServiceCompleted(event);

        // Assert
        verify(sdpContextFacade).fetchRequestServiceId(requestId);
        verify(sdpContextFacade).fetchServiceComponentRequirements(serviceId);
        verify(inventoryContextFacade).findComponentStock(technicianId, componentId);
        verify(inventoryContextFacade).updateComponentStock(technicianId, componentId, availableQty - requiredQty);
    }

    @Test
    @DisplayName("onServiceCompleted should skip if request service ID not found (AAA)")
    void onServiceCompleted_ShouldSkip_WhenServiceIdNotFound() {
        // Arrange
        ServiceCompletedEvent event = new ServiceCompletedEvent(1L, 100L, 200L);
        when(sdpContextFacade.fetchRequestServiceId(100L)).thenReturn(Optional.empty());

        // Act
        handler.onServiceCompleted(event);

        // Assert
        verify(sdpContextFacade).fetchRequestServiceId(100L);
        verifyNoMoreInteractions(sdpContextFacade);
        verifyNoInteractions(inventoryContextFacade);
    }

    @Test
    @DisplayName("onServiceCompleted should set to 0 if stock is insufficient (AAA)")
    void onServiceCompleted_ShouldSetToZero_WhenInsufficientStock() {
        // Arrange
        Long serviceOpId = 1L;
        Long requestId = 100L;
        Long technicianId = 200L;
        String serviceIdStr = "500";
        Long serviceId = 500L;
        Long componentId = 10L;
        int requiredQty = 10;
        int availableQty = 5; // Insufficient
        
        ServiceCompletedEvent event = new ServiceCompletedEvent(serviceOpId, requestId, technicianId);

        when(sdpContextFacade.fetchRequestServiceId(requestId)).thenReturn(Optional.of(serviceIdStr));
        
        ServiceComponentRequirement requirement = mock(ServiceComponentRequirement.class);
        when(requirement.componentId()).thenReturn(componentId);
        when(requirement.quantity()).thenReturn(requiredQty);
        
        when(sdpContextFacade.fetchServiceComponentRequirements(serviceId))
                .thenReturn(List.of(requirement));

        ComponentStockResource stockResource = mock(ComponentStockResource.class);
        when(stockResource.quantityAvailable()).thenReturn(availableQty);
        when(inventoryContextFacade.findComponentStock(technicianId, componentId))
                .thenReturn(Optional.of(stockResource));

        // Act
        handler.onServiceCompleted(event);

        // Assert
        verify(sdpContextFacade).fetchRequestServiceId(requestId);
        verify(sdpContextFacade).fetchServiceComponentRequirements(serviceId);
        verify(inventoryContextFacade).findComponentStock(technicianId, componentId);
        // Expecting update with 0 because available (5) - required (10) < 0
        verify(inventoryContextFacade).updateComponentStock(technicianId, componentId, 0);
    }
}
