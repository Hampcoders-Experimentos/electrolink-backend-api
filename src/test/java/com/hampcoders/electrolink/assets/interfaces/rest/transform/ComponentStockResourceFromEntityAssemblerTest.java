package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Component;
import com.hampcoders.electrolink.assets.domain.model.entities.ComponentStock;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentStockResource;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComponentStockResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a component stock, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenStockProvided() {
    // Arrange
    UUID stockId = UUID.randomUUID();
    Date lastUpdated = new Date();
    Component component = mock(Component.class);
    when(component.getComponentUid()).thenReturn(100L);
    when(component.getName()).thenReturn("Resistor");
    ComponentStock stock = mock(ComponentStock.class);
    when(stock.getId()).thenReturn(stockId);
    when(stock.getComponent()).thenReturn(component);
    when(stock.getQuantityAvailable()).thenReturn(15);
    when(stock.getAlertThreshold()).thenReturn(5);
    when(stock.getLastUpdated()).thenReturn(lastUpdated);

    // Act
    ComponentStockResource resource =
        ComponentStockResourceFromEntityAssembler.toResourceFromEntity(stock);

    // Assert
    assertEquals(stockId, resource.componentStockId());
    assertEquals(100L, resource.componentId());
    assertEquals("Resistor", resource.componentName());
    assertEquals(15, resource.quantityAvailable());
    assertEquals(5, resource.alertThreshold());
    assertEquals(lastUpdated, resource.lastUpdated());
  }

  @Test
  @DisplayName("Given a stock with different quantities, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentQuantitiesProvided() {
    // Arrange
    Component component = mock(Component.class);
    when(component.getComponentUid()).thenReturn(200L);
    when(component.getName()).thenReturn("Capacitor");
    ComponentStock stock = mock(ComponentStock.class);
    when(stock.getId()).thenReturn(UUID.randomUUID());
    when(stock.getComponent()).thenReturn(component);
    when(stock.getQuantityAvailable()).thenReturn(0);
    when(stock.getAlertThreshold()).thenReturn(1);
    when(stock.getLastUpdated()).thenReturn(new Date());

    // Act
    ComponentStockResource resource =
        ComponentStockResourceFromEntityAssembler.toResourceFromEntity(stock);

    // Assert
    assertEquals(200L, resource.componentId());
    assertEquals(0, resource.quantityAvailable());
    assertEquals(1, resource.alertThreshold());
  }

  @Test
  @DisplayName("Given a null stock, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenStockIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> ComponentStockResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
