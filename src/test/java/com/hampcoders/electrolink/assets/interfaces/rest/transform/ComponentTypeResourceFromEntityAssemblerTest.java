package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.ComponentType;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentTypeResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComponentTypeResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a component type, when assembling, then it maps id, name and description")
  void handle_ShouldMapAllFields_WhenTypeProvided() {
    // Arrange
    ComponentType type = mock(ComponentType.class);
    when(type.getId()).thenReturn(5L);
    when(type.getName()).thenReturn("Capacitors");
    when(type.getDescription()).thenReturn("desc");

    // Act
    ComponentTypeResource resource =
        ComponentTypeResourceFromEntityAssembler.toResourceFromEntity(type);

    // Assert
    assertEquals(5L, resource.componentTypeId());
    assertEquals("Capacitors", resource.name());
    assertEquals("desc", resource.description());
  }

  @Test
  @DisplayName("Given another component type, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentTypeProvided() {
    // Arrange
    ComponentType type = mock(ComponentType.class);
    when(type.getId()).thenReturn(8L);
    when(type.getName()).thenReturn("Resistors");
    when(type.getDescription()).thenReturn("other");

    // Act
    ComponentTypeResource resource =
        ComponentTypeResourceFromEntityAssembler.toResourceFromEntity(type);

    // Assert
    assertEquals(8L, resource.componentTypeId());
    assertEquals("Resistors", resource.name());
    assertEquals("other", resource.description());
  }

  @Test
  @DisplayName("Given a null component type, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenTypeIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> ComponentTypeResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
