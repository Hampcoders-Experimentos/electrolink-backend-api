package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Component;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComponentResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given an active component, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenComponentIsActive() {
    // Arrange
    Component component = mock(Component.class);
    when(component.getComponentUid()).thenReturn(100L);
    when(component.getName()).thenReturn("Resistor");
    when(component.getDescription()).thenReturn("desc");
    when(component.getIsActive()).thenReturn(true);
    when(component.getComponentTypeId()).thenReturn(5L);

    // Act
    ComponentResource resource =
        ComponentResourceFromEntityAssembler.toResourceFromEntity(component);

    // Assert
    assertEquals("100", resource.id());
    assertEquals("Resistor", resource.name());
    assertEquals("desc", resource.description());
    assertTrue(resource.isActive());
    assertEquals(5L, resource.componentTypeId());
  }

  @Test
  @DisplayName("Given an inactive component, when assembling, then isActive is false")
  void handle_ShouldMapInactive_WhenComponentIsInactive() {
    // Arrange
    Component component = mock(Component.class);
    when(component.getComponentUid()).thenReturn(101L);
    when(component.getName()).thenReturn("Capacitor");
    when(component.getDescription()).thenReturn("desc2");
    when(component.getIsActive()).thenReturn(false);
    when(component.getComponentTypeId()).thenReturn(6L);

    // Act
    ComponentResource resource =
        ComponentResourceFromEntityAssembler.toResourceFromEntity(component);

    // Assert
    assertFalse(resource.isActive());
  }

  @Test
  @DisplayName("Given a null component, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenComponentIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> ComponentResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
