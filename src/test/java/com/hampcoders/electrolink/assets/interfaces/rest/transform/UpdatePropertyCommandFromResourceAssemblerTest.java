package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.assets.domain.model.commands.UpdatePropertyCommand;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.AddressResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.UpdatePropertyResource;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdatePropertyCommandFromResourceAssemblerTest {

  private static AddressResource address() {
    return new AddressResource("Street", "1", "City", "00001", "PE", 1.0f, 2.0f);
  }

  @Test
  @DisplayName("Given a resource and id, when assembling, then it maps id, address, region and district")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    UUID propertyId = UUID.randomUUID();
    UpdatePropertyResource resource =
        new UpdatePropertyResource(address(), "Lima", "Miraflores");

    // Act
    UpdatePropertyCommand command =
        UpdatePropertyCommandFromResourceAssembler.toCommandFromResource(propertyId, resource);

    // Assert
    assertEquals(propertyId, command.propertyId());
    assertEquals("Lima", command.region().name());
    assertEquals("Miraflores", command.district().name());
    assertEquals("Street", command.address().street());
  }

  @Test
  @DisplayName("Given different values, when assembling, then it maps the new region and district")
  void handle_ShouldMapNewValues_WhenDifferentValuesProvided() {
    // Arrange
    UUID propertyId = UUID.randomUUID();
    UpdatePropertyResource resource =
        new UpdatePropertyResource(address(), "Cusco", "Wanchaq");

    // Act
    UpdatePropertyCommand command =
        UpdatePropertyCommandFromResourceAssembler.toCommandFromResource(propertyId, resource);

    // Assert
    assertEquals("Cusco", command.region().name());
    assertEquals("Wanchaq", command.district().name());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Arrange
    UUID propertyId = UUID.randomUUID();

    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> UpdatePropertyCommandFromResourceAssembler.toCommandFromResource(propertyId, null));
  }
}
