package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.monitoring.domain.model.commands.UpdateRatingCommand;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.UpdateRatingResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateRatingCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource, when assembling, then it maps id, score and comment")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    UpdateRatingResource resource = new UpdateRatingResource(50L, 4, "Good");

    // Act
    UpdateRatingCommand command =
        UpdateRatingCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(50L, command.ratingId());
    assertEquals(4, command.score());
    assertEquals("Good", command.comment());
  }

  @Test
  @DisplayName("Given null score and comment, when assembling, then it maps the null values")
  void handle_ShouldMapNulls_WhenScoreAndCommentAreNull() {
    // Arrange
    UpdateRatingResource resource = new UpdateRatingResource(50L, null, null);

    // Act
    UpdateRatingCommand command =
        UpdateRatingCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(50L, command.ratingId());
    assertNull(command.score());
    assertNull(command.comment());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> UpdateRatingCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
