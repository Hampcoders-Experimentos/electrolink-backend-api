package com.hampcoders.electrolink.assets.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.TechnicianInventory;
import com.hampcoders.electrolink.assets.domain.model.commands.CreateTechnicianInventoryCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.DeleteComponentStockCommand;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.ComponentRepository;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.TechnicianInventoryRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TechnicianInventoryCommandServiceImplTest {

  @Mock
  private TechnicianInventoryRepository technicianInventoryRepository;
  @Mock
  private ComponentRepository componentRepository;

  @InjectMocks
  private TechnicianInventoryCommandServiceImpl technicianInventoryCommandService;

  @Test
  @DisplayName("Given no existing inventory, when handling CreateTechnicianInventoryCommand, then it returns the saved id")
  void handle_ShouldReturnInventoryId_WhenCreated() {
    // Arrange
    CreateTechnicianInventoryCommand command =
        new CreateTechnicianInventoryCommand(new TechnicianId(7L));
    UUID inventoryId = UUID.randomUUID();
    TechnicianInventory saved = mock(TechnicianInventory.class);
    when(saved.getId()).thenReturn(inventoryId);
    when(technicianInventoryRepository.existsByTechnicianId(7L)).thenReturn(false);
    when(technicianInventoryRepository.save(any(TechnicianInventory.class))).thenReturn(saved);

    // Act
    UUID result = technicianInventoryCommandService.handle(command);

    // Assert
    assertEquals(inventoryId, result);
  }

  @Test
  @DisplayName("Given an existing inventory, when handling CreateTechnicianInventoryCommand, then it throws IllegalState")
  void handle_ShouldThrow_WhenInventoryAlreadyExists() {
    // Arrange
    CreateTechnicianInventoryCommand command =
        new CreateTechnicianInventoryCommand(new TechnicianId(7L));
    when(technicianInventoryRepository.existsByTechnicianId(7L)).thenReturn(true);

    // Act & Assert
    assertThrows(IllegalStateException.class,
        () -> technicianInventoryCommandService.handle(command));
  }

  @Test
  @DisplayName("Given a missing inventory, when handling DeleteComponentStockCommand, then it returns false")
  void handle_ShouldReturnFalse_WhenDeletingStockForMissingInventory() {
    // Arrange
    DeleteComponentStockCommand command = new DeleteComponentStockCommand(7L, 10L);
    when(technicianInventoryRepository.findByTechnicianId(7L)).thenReturn(Optional.empty());

    // Act
    Boolean result = technicianInventoryCommandService.handle(command);

    // Assert
    assertFalse(result);
  }
}
