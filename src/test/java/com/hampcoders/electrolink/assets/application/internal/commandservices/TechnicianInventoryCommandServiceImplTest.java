package com.hampcoders.electrolink.assets.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Component;
import com.hampcoders.electrolink.assets.domain.model.aggregates.TechnicianInventory;
import com.hampcoders.electrolink.assets.domain.model.commands.AddComponentStockCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.CreateTechnicianInventoryCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.DeleteComponentStockCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.UpdateComponentStockCommand;
import com.hampcoders.electrolink.assets.domain.model.entities.ComponentStock;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.ComponentRepository;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.TechnicianInventoryRepository;
import jakarta.persistence.EntityNotFoundException;
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

  @Test
  @DisplayName("Given an inventory and component, when handling AddComponentStockCommand, then it adds and saves the stock")
  void handle_ShouldAddStock_WhenInventoryAndComponentExist() {
    // Arrange
    AddComponentStockCommand command = new AddComponentStockCommand(7L, 10L, 5, 2);
    TechnicianInventory inventory = mock(TechnicianInventory.class);
    Component component = mock(Component.class);
    when(technicianInventoryRepository.findByTechnicianId(7L)).thenReturn(Optional.of(inventory));
    when(componentRepository.findByComponentUid(10L)).thenReturn(Optional.of(component));
    when(technicianInventoryRepository.save(inventory)).thenReturn(inventory);

    // Act
    Optional<TechnicianInventory> result = technicianInventoryCommandService.handle(command);

    // Assert
    assertTrue(result.isPresent());
    assertSame(inventory, result.get());
    verify(inventory).addToStock(component, 5, 2);
  }

  @Test
  @DisplayName("Given a missing inventory, when handling AddComponentStockCommand, then it throws EntityNotFound")
  void handle_ShouldThrow_WhenInventoryMissingOnAdd() {
    // Arrange
    AddComponentStockCommand command = new AddComponentStockCommand(7L, 10L, 5, 2);
    when(technicianInventoryRepository.findByTechnicianId(7L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> technicianInventoryCommandService.handle(command));
  }

  @Test
  @DisplayName("Given a missing component, when handling AddComponentStockCommand, then it throws EntityNotFound")
  void handle_ShouldThrow_WhenComponentMissingOnAdd() {
    // Arrange
    AddComponentStockCommand command = new AddComponentStockCommand(7L, 10L, 5, 2);
    when(technicianInventoryRepository.findByTechnicianId(7L))
        .thenReturn(Optional.of(mock(TechnicianInventory.class)));
    when(componentRepository.findByComponentUid(10L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> technicianInventoryCommandService.handle(command));
  }

  @Test
  @DisplayName("Given an existing stock item, when handling UpdateComponentStockCommand, then it updates and saves it")
  void handle_ShouldUpdateStock_WhenStockItemExists() {
    // Arrange
    UpdateComponentStockCommand command = new UpdateComponentStockCommand(7L, 10L, 20, 5);
    Component component = mock(Component.class);
    when(component.getComponentUid()).thenReturn(10L);
    ComponentStock stock = mock(ComponentStock.class);
    when(stock.getComponent()).thenReturn(component);
    TechnicianInventory inventory = mock(TechnicianInventory.class);
    when(inventory.getComponentStocks()).thenReturn(List.of(stock));
    when(technicianInventoryRepository.findByTechnicianId(7L)).thenReturn(Optional.of(inventory));

    // Act
    Optional<TechnicianInventory> result = technicianInventoryCommandService.handle(command);

    // Assert
    assertTrue(result.isPresent());
    verify(stock).updateQuantity(20);
    verify(stock).updateAlertThreshold(5);
    verify(technicianInventoryRepository).save(inventory);
  }

  @Test
  @DisplayName("Given a stock item not in the inventory, when handling UpdateComponentStockCommand, then it throws EntityNotFound")
  void handle_ShouldThrow_WhenStockItemNotInInventory() {
    // Arrange
    UpdateComponentStockCommand command = new UpdateComponentStockCommand(7L, 10L, 20, 5);
    TechnicianInventory inventory = mock(TechnicianInventory.class);
    when(inventory.getComponentStocks()).thenReturn(List.of());
    when(technicianInventoryRepository.findByTechnicianId(7L)).thenReturn(Optional.of(inventory));

    // Act & Assert
    assertThrows(EntityNotFoundException.class,
        () -> technicianInventoryCommandService.handle(command));
  }

  @Test
  @DisplayName("Given a removable stock item, when handling DeleteComponentStockCommand, then it removes and saves")
  void handle_ShouldRemoveStock_WhenItemRemoved() {
    // Arrange
    DeleteComponentStockCommand command = new DeleteComponentStockCommand(7L, 10L);
    TechnicianInventory inventory = mock(TechnicianInventory.class);
    when(inventory.removeStockItem(10L)).thenReturn(true);
    when(technicianInventoryRepository.findByTechnicianId(7L)).thenReturn(Optional.of(inventory));

    // Act
    Boolean result = technicianInventoryCommandService.handle(command);

    // Assert
    assertTrue(result);
    verify(technicianInventoryRepository).save(inventory);
  }
}
