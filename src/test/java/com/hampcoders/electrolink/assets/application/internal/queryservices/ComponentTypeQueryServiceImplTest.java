package com.hampcoders.electrolink.assets.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.ComponentType;
import com.hampcoders.electrolink.assets.domain.model.queries.GetAllComponentTypesQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetComponentTypeByIdQuery;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.ComponentTypeId;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.ComponentTypeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComponentTypeQueryServiceImplTest {

  @Mock
  private ComponentTypeRepository componentTypeRepository;

  @InjectMocks
  private ComponentTypeQueryServiceImpl componentTypeQueryService;

  @Test
  @DisplayName("Given an existing id, when handling GetComponentTypeByIdQuery, then it returns the type")
  void handle_ShouldReturnType_WhenIdExists() {
    // Arrange
    ComponentType type = mock(ComponentType.class);
    when(componentTypeRepository.findById(5L)).thenReturn(Optional.of(type));

    // Act
    Optional<ComponentType> result =
        componentTypeQueryService.handle(new GetComponentTypeByIdQuery(new ComponentTypeId(5L)));

    // Assert
    assertTrue(result.isPresent());
    assertSame(type, result.get());
  }

  @Test
  @DisplayName("Given a missing id, when handling GetComponentTypeByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenIdMissing() {
    // Arrange
    when(componentTypeRepository.findById(5L)).thenReturn(Optional.empty());

    // Act
    Optional<ComponentType> result =
        componentTypeQueryService.handle(new GetComponentTypeByIdQuery(new ComponentTypeId(5L)));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given existing types, when handling GetAllComponentTypesQuery, then it returns all of them")
  void handle_ShouldReturnAllTypes_WhenQueryingAll() {
    // Arrange
    List<ComponentType> types = List.of(mock(ComponentType.class));
    when(componentTypeRepository.findAll()).thenReturn(types);

    // Act
    List<ComponentType> result = componentTypeQueryService.handle(new GetAllComponentTypesQuery());

    // Assert
    assertEquals(types, result);
  }
}
