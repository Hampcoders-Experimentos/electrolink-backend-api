package com.hampcoders.electrolink.assets.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.TechnicianInventory;
import com.hampcoders.electrolink.assets.domain.model.entities.ComponentStock;
import com.hampcoders.electrolink.assets.domain.model.queries.GetInventoryByTechnicianIdQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetStockItemDetailsQuery;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.ComponentId;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.ComponentStockRepository;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.TechnicianInventoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TechnicianInventoryQueryServiceImplTest {

  @Mock
  private TechnicianInventoryRepository technicianInventoryRepository;
  @Mock
  private ComponentStockRepository componentStockRepository;

  @InjectMocks
  private TechnicianInventoryQueryServiceImpl technicianInventoryQueryService;

  @Test
  @DisplayName("Given an existing inventory, when handling GetInventoryByTechnicianIdQuery, then it returns the inventory")
  void handle_ShouldReturnInventory_WhenTechnicianHasInventory() {
    // Arrange
    TechnicianInventory inventory = mock(TechnicianInventory.class);
    when(technicianInventoryRepository.findByTechnicianIdWithStocks(7L))
        .thenReturn(Optional.of(inventory));

    // Act
    Optional<TechnicianInventory> result = technicianInventoryQueryService.handle(
        new GetInventoryByTechnicianIdQuery(new TechnicianId(7L)));

    // Assert
    assertTrue(result.isPresent());
    assertSame(inventory, result.get());
  }

  @Test
  @DisplayName("Given no inventory, when handling GetInventoryByTechnicianIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenInventoryMissing() {
    // Arrange
    when(technicianInventoryRepository.findByTechnicianIdWithStocks(7L))
        .thenReturn(Optional.empty());

    // Act
    Optional<TechnicianInventory> result = technicianInventoryQueryService.handle(
        new GetInventoryByTechnicianIdQuery(new TechnicianId(7L)));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given an existing stock item, when handling GetStockItemDetailsQuery, then it returns the stock")
  void handle_ShouldReturnStock_WhenStockItemExists() {
    // Arrange
    ComponentStock stock = mock(ComponentStock.class);
    when(componentStockRepository.findByTechnicianInventoryIdAndComponentUid(7L, 10L))
        .thenReturn(Optional.of(stock));

    // Act
    Optional<ComponentStock> result = technicianInventoryQueryService.handle(
        new GetStockItemDetailsQuery(new TechnicianId(7L), new ComponentId(10L)));

    // Assert
    assertTrue(result.isPresent());
    assertSame(stock, result.get());
  }
}
