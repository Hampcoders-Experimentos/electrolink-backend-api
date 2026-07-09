package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.monitoring.domain.model.commands.AddRatingCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.CreateRatingResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateRatingCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource, when assembling, then it maps all rating fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    CreateRatingResource resource = new CreateRatingResource(1L, 4, "Good", "rater-1", 2L);

    // Act
    AddRatingCommand command =
        CreateRatingCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(new RequestId(1L), command.requestId());
    assertEquals(4, command.score());
    assertEquals("Good", command.comment());
    assertEquals("rater-1", command.raterId());
    assertEquals(new TechnicianId(2L), command.technicianId());
  }

  @Test
  @DisplayName("Given different values, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentResourceProvided() {
    // Arrange
    CreateRatingResource resource = new CreateRatingResource(9L, 1, "Bad", "rater-2", 8L);

    // Act
    AddRatingCommand command =
        CreateRatingCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(new RequestId(9L), command.requestId());
    assertEquals(1, command.score());
    assertEquals(new TechnicianId(8L), command.technicianId());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreateRatingCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
