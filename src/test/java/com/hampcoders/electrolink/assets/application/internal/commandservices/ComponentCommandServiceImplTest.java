package com.hampcoders.electrolink.assets.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Component;
import com.hampcoders.electrolink.assets.domain.model.commands.CreateComponentCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.DeleteComponentCommand;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.ComponentId;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.ComponentRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComponentCommandServiceImplTest {

  @Mock
  private ComponentRepository componentRepository;

  @InjectMocks
  private ComponentCommandServiceImpl componentCommandService;

  @Test
  @DisplayName("Given a new name, when handling CreateComponentCommand, then it returns the saved component id")
  void handle_ShouldReturnComponentId_WhenComponentCreated() {
    // Arrange
    CreateComponentCommand command =
        new CreateComponentCommand(UUID.randomUUID(), "Resistor", "desc", 5L, true);
    Component saved = mock(Component.class);
    when(saved.getComponentUid()).thenReturn(100L);
    when(componentRepository.existsByName("Resistor")).thenReturn(false);
    when(componentRepository.save(any(Component.class))).thenReturn(saved);

    // Act
    ComponentId result = componentCommandService.handle(command);

    // Assert
    assertEquals(new ComponentId(100L), result);
  }

  @Test
  @DisplayName("Given an existing name, when handling CreateComponentCommand, then it throws IllegalState")
  void handle_ShouldThrow_WhenComponentNameExists() {
    // Arrange
    CreateComponentCommand command =
        new CreateComponentCommand(UUID.randomUUID(), "Resistor", "desc", 5L, true);
    when(componentRepository.existsByName("Resistor")).thenReturn(true);

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> componentCommandService.handle(command));
  }

  @Test
  @DisplayName("Given a missing component, when handling DeleteComponentCommand, then it returns false")
  void handle_ShouldReturnFalse_WhenDeletingMissingComponent() {
    // Arrange
    DeleteComponentCommand command = new DeleteComponentCommand(7L);
    when(componentRepository.existsById(7L)).thenReturn(false);

    // Act
    Boolean result = componentCommandService.handle(command);

    // Assert
    assertFalse(result);
  }
}
