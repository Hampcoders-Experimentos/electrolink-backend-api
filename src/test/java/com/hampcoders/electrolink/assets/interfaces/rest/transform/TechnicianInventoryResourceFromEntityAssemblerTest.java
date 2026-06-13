package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Component;
import com.hampcoders.electrolink.assets.domain.model.aggregates.TechnicianInventory;
import com.hampcoders.electrolink.assets.domain.model.entities.ComponentStock;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.TechnicianInventoryResource;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TechnicianInventoryResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given an inventory with stock, when assembling, then it maps id, technician and stock")
  void handle_ShouldMapInventoryWithStock_WhenStockExists() {
    // Arrange
    UUID inventoryId = UUID.randomUUID();
    Component component = mock(Component.class);
    when(component.getComponentUid()).thenReturn(100L);
    when(component.getName()).thenReturn("Resistor");
    ComponentStock stock = mock(ComponentStock.class);
    when(stock.getId()).thenReturn(UUID.randomUUID());
    when(stock.getComponent()).thenReturn(component);
    when(stock.getQuantityAvailable()).thenReturn(15);
    when(stock.getAlertThreshold()).thenReturn(5);
    when(stock.getLastUpdated()).thenReturn(new Date());
    TechnicianInventory inventory = mock(TechnicianInventory.class);
    when(inventory.getId()).thenReturn(inventoryId);
    when(inventory.getTechnicianId()).thenReturn(7L);
    when(inventory.getComponentStocks()).thenReturn(List.of(stock));

    // Act
    TechnicianInventoryResource resource =
        TechnicianInventoryResourceFromEntityAssembler.toResourceFromEntity(inventory);

    // Assert
    assertEquals(inventoryId, resource.inventoryId());
    assertEquals(7L, resource.technicianId());
    assertEquals(1, resource.stock().size());
    assertEquals(100L, resource.stock().getFirst().componentId());
  }

  @Test
  @DisplayName("Given an inventory without stock, when assembling, then the stock list is empty")
  void handle_ShouldMapEmptyStock_WhenNoStockExists() {
    // Arrange
    TechnicianInventory inventory = mock(TechnicianInventory.class);
    when(inventory.getId()).thenReturn(UUID.randomUUID());
    when(inventory.getTechnicianId()).thenReturn(7L);
    when(inventory.getComponentStocks()).thenReturn(List.of());

    // Act
    TechnicianInventoryResource resource =
        TechnicianInventoryResourceFromEntityAssembler.toResourceFromEntity(inventory);

    // Assert
    assertTrue(resource.stock().isEmpty());
  }

  @Test
  @DisplayName("Given a null inventory, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenInventoryIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> TechnicianInventoryResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
