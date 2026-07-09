package com.hampcoders.electrolink.assets.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.ComponentType;
import com.hampcoders.electrolink.assets.domain.model.commands.CreateComponentTypeCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.DeleteComponentTypeCommand;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.ComponentTypeId;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.ComponentRepository;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.ComponentTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComponentTypeCommandServiceImplTest {

  @Mock
  private ComponentTypeRepository componentTypeRepository;
  @Mock
  private ComponentRepository componentRepository;

  @InjectMocks
  private ComponentTypeCommandServiceImpl componentTypeCommandService;

  @Test
  @DisplayName("Given a new name, when handling CreateComponentTypeCommand, then it returns the saved type id")
  void handle_ShouldReturnComponentTypeId_WhenCreated() {
    // Arrange
    CreateComponentTypeCommand command = new CreateComponentTypeCommand("Capacitors", "desc");
    ComponentType saved = mock(ComponentType.class);
    when(saved.getId()).thenReturn(5L);
    when(componentTypeRepository.existsByName("Capacitors")).thenReturn(false);
    when(componentTypeRepository.save(any(ComponentType.class))).thenReturn(saved);

    // Act
    ComponentTypeId result = componentTypeCommandService.handle(command);

    // Assert
    assertEquals(new ComponentTypeId(5L), result);
  }

  @Test
  @DisplayName("Given an existing name, when handling CreateComponentTypeCommand, then it throws IllegalState")
  void handle_ShouldThrow_WhenTypeNameExists() {
    // Arrange
    CreateComponentTypeCommand command = new CreateComponentTypeCommand("Capacitors", "desc");
    when(componentTypeRepository.existsByName("Capacitors")).thenReturn(true);

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> componentTypeCommandService.handle(command));
  }

  @Test
  @DisplayName("Given a type in use, when handling DeleteComponentTypeCommand, then it throws IllegalState")
  void handle_ShouldThrow_WhenDeletingTypeInUse() {
    // Arrange
    DeleteComponentTypeCommand command = new DeleteComponentTypeCommand(5L);
    when(componentTypeRepository.existsById(5L)).thenReturn(true);
    when(componentRepository.existsByComponentTypeId(new ComponentTypeId(5L))).thenReturn(true);

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> componentTypeCommandService.handle(command));
  }
}
