package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.assets.domain.model.commands.CreatePropertyCommand;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.OwnerId;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.AddressResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreatePropertyResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreatePropertyCommandFromResourceAssemblerTest {

  private static AddressResource address() {
    return new AddressResource("Street", "1", "City", "00001", "PE", 1.0f, 2.0f);
  }

  @Test
  @DisplayName("Given a valid resource, when assembling, then it maps owner, address, region and district")
  void handle_ShouldMapAllFields_WhenResourceIsValid() {
    // Arrange
    CreatePropertyResource resource =
        new CreatePropertyResource("9", address(), "Lima", "Miraflores");

    // Act
    CreatePropertyCommand command =
        CreatePropertyCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(new OwnerId(9L), command.ownerId());
    assertEquals("Lima", command.region().name());
    assertEquals("Miraflores", command.district().name());
    assertEquals("Street", command.address().street());
  }

  @Test
  @DisplayName("Given a different owner id, when assembling, then it maps that owner id")
  void handle_ShouldMapNewOwner_WhenDifferentOwnerProvided() {
    // Arrange
    CreatePropertyResource resource =
        new CreatePropertyResource("42", address(), "Cusco", "Wanchaq");

    // Act
    CreatePropertyCommand command =
        CreatePropertyCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(new OwnerId(42L), command.ownerId());
    assertEquals("Cusco", command.region().name());
  }

  @Test
  @DisplayName("Given a non-numeric owner id, when assembling, then it throws NumberFormatException")
  void handle_ShouldThrowNumberFormat_WhenOwnerIdIsNotNumeric() {
    // Arrange
    CreatePropertyResource resource =
        new CreatePropertyResource("abc", address(), "Lima", "Miraflores");

    // Act & Assert
    assertThrows(NumberFormatException.class,
        () -> CreatePropertyCommandFromResourceAssembler.toCommandFromResource(resource));
  }
}
