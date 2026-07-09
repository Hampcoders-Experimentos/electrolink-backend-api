package com.hampcoders.electrolink.assets.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Component;
import com.hampcoders.electrolink.assets.domain.model.queries.GetAllComponentsQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetComponentByIdQuery;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.ComponentId;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.ComponentRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComponentQueryServiceImplTest {

  @Mock
  private ComponentRepository componentRepository;

  @InjectMocks
  private ComponentQueryServiceImpl componentQueryService;

  @Test
  @DisplayName("Given an existing id, when handling GetComponentByIdQuery, then it returns the component")
  void handle_ShouldReturnComponent_WhenIdExists() {
    // Arrange
    Component component = mock(Component.class);
    when(componentRepository.findById(1L)).thenReturn(Optional.of(component));

    // Act
    Optional<Component> result =
        componentQueryService.handle(new GetComponentByIdQuery(new ComponentId(1L)));

    // Assert
    assertTrue(result.isPresent());
    assertSame(component, result.get());
  }

  @Test
  @DisplayName("Given a missing id, when handling GetComponentByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenIdMissing() {
    // Arrange
    when(componentRepository.findById(1L)).thenReturn(Optional.empty());

    // Act
    Optional<Component> result =
        componentQueryService.handle(new GetComponentByIdQuery(new ComponentId(1L)));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given existing components, when handling GetAllComponentsQuery, then it returns all of them")
  void handle_ShouldReturnAllComponents_WhenQueryingAll() {
    // Arrange
    List<Component> components = List.of(mock(Component.class), mock(Component.class));
    when(componentRepository.findAll()).thenReturn(components);

    // Act
    List<Component> result = componentQueryService.handle(new GetAllComponentsQuery());

    // Assert
    assertEquals(components, result);
  }
}
