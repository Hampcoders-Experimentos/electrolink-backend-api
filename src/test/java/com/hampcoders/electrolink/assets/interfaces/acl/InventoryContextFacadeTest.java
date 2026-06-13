package com.hampcoders.electrolink.assets.interfaces.acl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.TechnicianInventory;
import com.hampcoders.electrolink.assets.domain.model.commands.CreateTechnicianInventoryCommand;
import com.hampcoders.electrolink.assets.domain.model.queries.GetInventoryByTechnicianIdQuery;
import com.hampcoders.electrolink.assets.domain.services.TechnicianInventoryCommandService;
import com.hampcoders.electrolink.assets.domain.services.TechnicianInventoryQueryService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryContextFacadeTest {

  @Mock
  private TechnicianInventoryCommandService technicianInventoryCommandService;
  @Mock
  private TechnicianInventoryQueryService technicianInventoryQueryService;

  @InjectMocks
  private InventoryContextFacade inventoryContextFacade;

  @Test
  @DisplayName("Given the command service returns an id, when creating an inventory, then it returns that id")
  void handle_ShouldReturnInventoryId_WhenCreatingInventory() {
    // Arrange
    UUID inventoryId = UUID.randomUUID();
    when(technicianInventoryCommandService.handle(any(CreateTechnicianInventoryCommand.class)))
        .thenReturn(inventoryId);

    // Act
    UUID result = inventoryContextFacade.createInventoryForTechnician(7L);

    // Assert
    assertEquals(inventoryId, result);
  }

  @Test
  @DisplayName("Given no inventory, when checking existence, then it returns false")
  void handle_ShouldReturnFalse_WhenInventoryDoesNotExist() {
    // Arrange
    when(technicianInventoryQueryService.handle(any(GetInventoryByTechnicianIdQuery.class)))
        .thenReturn(Optional.empty());

    // Act
    boolean result = inventoryContextFacade.existsInventoryForTechnician(7L);

    // Assert
    assertFalse(result);
  }

  @Test
  @DisplayName("Given an existing inventory, when checking existence, then it returns true")
  void handle_ShouldReturnTrue_WhenInventoryExists() {
    // Arrange
    when(technicianInventoryQueryService.handle(any(GetInventoryByTechnicianIdQuery.class)))
        .thenReturn(Optional.of(mock(TechnicianInventory.class)));

    // Act
    boolean result = inventoryContextFacade.existsInventoryForTechnician(7L);

    // Assert
    assertTrue(result);
  }
}
