package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Component;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentLookupResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComponentLookupResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a component, when assembling lookup, then it maps uid and name")
  void handle_ShouldMapUidAndName_WhenComponentProvided() {
    // Arrange
    Component component = mock(Component.class);
    when(component.getComponentUid()).thenReturn(100L);
    when(component.getName()).thenReturn("Resistor");

    // Act
    ComponentLookupResource resource =
        ComponentLookupResourceFromEntityAssembler.toResource(component);

    // Assert
    assertEquals(100L, resource.id());
    assertEquals("Resistor", resource.name());
  }

  @Test
  @DisplayName("Given another component, when assembling lookup, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentComponentProvided() {
    // Arrange
    Component component = mock(Component.class);
    when(component.getComponentUid()).thenReturn(200L);
    when(component.getName()).thenReturn("Capacitor");

    // Act
    ComponentLookupResource resource =
        ComponentLookupResourceFromEntityAssembler.toResource(component);

    // Assert
    assertEquals(200L, resource.id());
    assertEquals("Capacitor", resource.name());
  }

  @Test
  @DisplayName("Given a null component, when assembling lookup, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenComponentIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> ComponentLookupResourceFromEntityAssembler.toResource(null));
  }
}
