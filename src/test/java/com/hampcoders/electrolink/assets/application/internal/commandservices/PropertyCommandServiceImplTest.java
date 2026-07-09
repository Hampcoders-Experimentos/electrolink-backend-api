package com.hampcoders.electrolink.assets.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Property;
import com.hampcoders.electrolink.assets.domain.model.commands.CreatePropertyCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.DeletePropertyCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.UpdatePropertyCommand;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.Address;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.District;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.OwnerId;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.Region;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.PropertyRepository;
import com.hampcoders.electrolink.shared.test.util.ReflectionTestUtils;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyCommandServiceImplTest {

  @Mock
  private PropertyRepository propertyRepository;

  @InjectMocks
  private PropertyCommandServiceImpl propertyCommandService;

  private static CreatePropertyCommand createCommand() {
    return new CreatePropertyCommand(
        new OwnerId(9L),
        new Address("Street", "1", "City", "00001", "PE", 0f, 0f),
        new Region("Lima"),
        new District("Miraflores"));
  }

  @Test
  @DisplayName("Given a valid command, when handling CreatePropertyCommand, then it returns the generated id")
  void handle_ShouldReturnPropertyId_WhenCreated() {
    // Arrange
    UUID generatedId = UUID.randomUUID();
    when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> {
      Property property = invocation.getArgument(0);
      ReflectionTestUtils.setField(property, "id", generatedId);
      return property;
    });

    // Act
    UUID result = propertyCommandService.handle(createCommand());

    // Assert
    assertEquals(generatedId, result);
  }

  @Test
  @DisplayName("Given a missing property, when handling UpdatePropertyCommand, then it returns empty")
  void handle_ShouldReturnEmpty_WhenUpdatingMissingProperty() {
    // Arrange
    UUID propertyId = UUID.randomUUID();
    UpdatePropertyCommand command = new UpdatePropertyCommand(
        propertyId,
        new Address("Street", "1", "City", "00001", "PE", 0f, 0f),
        new Region("Lima"),
        new District("Miraflores"));
    when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

    // Act
    Optional<Property> result = propertyCommandService.handle(command);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a missing property, when handling DeletePropertyCommand, then it returns false")
  void handle_ShouldReturnFalse_WhenDeletingMissingProperty() {
    // Arrange
    UUID propertyId = UUID.randomUUID();
    DeletePropertyCommand command = new DeletePropertyCommand(propertyId);
    when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

    // Act
    Boolean result = propertyCommandService.handle(command);

    // Assert
    assertFalse(result);
  }
}
