package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Property;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.Address;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.District;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.OwnerId;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.Region;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.PropertyResource;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PropertyResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a property, when assembling, then it maps id, owner, address, region and district")
  void handle_ShouldMapAllFields_WhenPropertyProvided() {
    // Arrange
    UUID propertyId = UUID.randomUUID();
    Property property = mock(Property.class);
    when(property.getId()).thenReturn(propertyId);
    when(property.getOwnerId()).thenReturn(new OwnerId(9L));
    when(property.getAddress())
        .thenReturn(new Address("Street", "1", "City", "00001", "PE", 1.0f, 2.0f));
    when(property.getRegion()).thenReturn(new Region("Lima"));
    when(property.getDistrict()).thenReturn(new District("Miraflores"));

    // Act
    PropertyResource resource =
        PropertyResourceFromEntityAssembler.toResourceFromEntity(property);

    // Assert
    assertEquals(propertyId.toString(), resource.id());
    assertEquals("9", resource.ownerId());
    assertEquals("Street", resource.address().street());
    assertEquals("Lima", resource.region().name());
    assertEquals("Miraflores", resource.district().name());
  }

  @Test
  @DisplayName("Given a property with another owner, when assembling, then it maps the new owner id")
  void handle_ShouldMapNewOwner_WhenDifferentOwnerProvided() {
    // Arrange
    Property property = mock(Property.class);
    when(property.getId()).thenReturn(UUID.randomUUID());
    when(property.getOwnerId()).thenReturn(new OwnerId(42L));
    when(property.getAddress())
        .thenReturn(new Address("Av", "2", "City", "00002", "PE", 0.0f, 0.0f));
    when(property.getRegion()).thenReturn(new Region("Cusco"));
    when(property.getDistrict()).thenReturn(new District("Wanchaq"));

    // Act
    PropertyResource resource =
        PropertyResourceFromEntityAssembler.toResourceFromEntity(property);

    // Assert
    assertEquals("42", resource.ownerId());
    assertEquals("Cusco", resource.region().name());
  }

  @Test
  @DisplayName("Given a null property, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenPropertyIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> PropertyResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
