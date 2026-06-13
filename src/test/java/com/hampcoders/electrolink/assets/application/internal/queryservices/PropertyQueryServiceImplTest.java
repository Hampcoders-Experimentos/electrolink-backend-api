package com.hampcoders.electrolink.assets.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Property;
import com.hampcoders.electrolink.assets.domain.model.queries.GetAllPropertiesByOwnerIdQuery;
import com.hampcoders.electrolink.assets.domain.model.queries.GetPropertyByIdQuery;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.OwnerId;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.PropertyRepository;
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
class PropertyQueryServiceImplTest {

  @Mock
  private PropertyRepository propertyRepository;

  @InjectMocks
  private PropertyQueryServiceImpl propertyQueryService;

  @Test
  @DisplayName("Given an existing id, when handling GetPropertyByIdQuery, then it returns the property")
  void handle_ShouldReturnProperty_WhenIdExists() {
    // Arrange
    UUID propertyId = UUID.randomUUID();
    Property property = mock(Property.class);
    when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

    // Act
    Optional<Property> result = propertyQueryService.handle(new GetPropertyByIdQuery(propertyId));

    // Assert
    assertTrue(result.isPresent());
    assertSame(property, result.get());
  }

  @Test
  @DisplayName("Given a missing id, when handling GetPropertyByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenIdMissing() {
    // Arrange
    UUID propertyId = UUID.randomUUID();
    when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

    // Act
    Optional<Property> result = propertyQueryService.handle(new GetPropertyByIdQuery(propertyId));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given an owner with properties, when handling GetAllPropertiesByOwnerIdQuery, then it returns them")
  void handle_ShouldReturnPropertiesByOwner_WhenOwnerHasProperties() {
    // Arrange
    OwnerId ownerId = new OwnerId(9L);
    List<Property> properties = List.of(mock(Property.class));
    when(propertyRepository.findPropertiesByOwnerId(ownerId)).thenReturn(properties);

    // Act
    List<Property> result =
        propertyQueryService.handle(new GetAllPropertiesByOwnerIdQuery(ownerId));

    // Assert
    assertEquals(properties, result);
  }
}
