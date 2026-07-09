package com.hampcoders.electrolink.profiles.application.internal.outboundservices;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.interfaces.acl.InventoryContextFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalAssetsServiceTest {

  @Mock
  private InventoryContextFacade inventoryContextFacade;

  @InjectMocks
  private ExternalAssetsService externalAssetsService;

  @Test
  @DisplayName("Given no existing inventory, when creating for a technician, then it creates the inventory")
  void handle_ShouldCreateInventory_WhenNotExisting() {
    // Arrange
    when(inventoryContextFacade.existsInventoryForTechnician(7L)).thenReturn(false);

    // Act
    externalAssetsService.createInventoryForTechnician(7L);

    // Assert
    verify(inventoryContextFacade).createInventoryForTechnician(7L);
  }

  @Test
  @DisplayName("Given an existing inventory, when creating for a technician, then it does not create again")
  void handle_ShouldSkipCreation_WhenAlreadyExists() {
    // Arrange
    when(inventoryContextFacade.existsInventoryForTechnician(7L)).thenReturn(true);

    // Act
    externalAssetsService.createInventoryForTechnician(7L);

    // Assert
    verify(inventoryContextFacade, never()).createInventoryForTechnician(7L);
  }

  @Test
  @DisplayName("Given no inventory for another technician, when creating, then it creates for that technician")
  void handle_ShouldCreateInventory_WhenDifferentTechnician() {
    // Arrange
    when(inventoryContextFacade.existsInventoryForTechnician(9L)).thenReturn(false);

    // Act
    externalAssetsService.createInventoryForTechnician(9L);

    // Assert
    verify(inventoryContextFacade).createInventoryForTechnician(9L);
  }
}
