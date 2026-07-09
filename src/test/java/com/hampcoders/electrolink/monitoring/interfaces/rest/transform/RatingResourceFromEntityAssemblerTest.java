package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Rating;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.RatingResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RatingResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a rating, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenRatingProvided() {
    // Arrange
    Rating rating = mock(Rating.class);
    when(rating.getId()).thenReturn(50L);
    when(rating.getRequestId()).thenReturn(new RequestId(1L));
    when(rating.getScore()).thenReturn(4);
    when(rating.getComment()).thenReturn("Good");
    when(rating.getRaterId()).thenReturn("rater-1");
    when(rating.getTechnicianId()).thenReturn(new TechnicianId(2L));

    // Act
    RatingResource resource = RatingResourceFromEntityAssembler.toResourceFromEntity(rating);

    // Assert
    assertEquals(50L, resource.id());
    assertEquals(1L, resource.requestId());
    assertEquals(4, resource.score());
    assertEquals("Good", resource.comment());
    assertEquals("rater-1", resource.raterId());
    assertEquals(2L, resource.technicianId());
  }

  @Test
  @DisplayName("Given another rating, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentRatingProvided() {
    // Arrange
    Rating rating = mock(Rating.class);
    when(rating.getId()).thenReturn(60L);
    when(rating.getRequestId()).thenReturn(new RequestId(9L));
    when(rating.getScore()).thenReturn(1);
    when(rating.getComment()).thenReturn("Bad");
    when(rating.getRaterId()).thenReturn("rater-2");
    when(rating.getTechnicianId()).thenReturn(new TechnicianId(8L));

    // Act
    RatingResource resource = RatingResourceFromEntityAssembler.toResourceFromEntity(rating);

    // Assert
    assertEquals(60L, resource.id());
    assertEquals(9L, resource.requestId());
    assertEquals(8L, resource.technicianId());
  }

  @Test
  @DisplayName("Given a null rating, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenRatingIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> RatingResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
