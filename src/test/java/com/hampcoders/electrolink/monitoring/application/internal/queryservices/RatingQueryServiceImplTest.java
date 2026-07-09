package com.hampcoders.electrolink.monitoring.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Rating;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetAllRatingsQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingByIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingsByTechnicianIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.RatingRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RatingQueryServiceImplTest {

  @Mock
  private RatingRepository ratingRepository;

  @InjectMocks
  private RatingQueryServiceImpl ratingQueryService;

  @Test
  @DisplayName("Given existing ratings, when handling GetAllRatingsQuery, then it returns all of them")
  void handle_ShouldReturnAllRatings_WhenQueryingAll() {
    // Arrange
    List<Rating> ratings = List.of(mock(Rating.class), mock(Rating.class));
    when(ratingRepository.findAll()).thenReturn(ratings);

    // Act
    List<Rating> result = ratingQueryService.handle(new GetAllRatingsQuery());

    // Assert
    assertEquals(ratings, result);
  }

  @Test
  @DisplayName("Given a missing id, when handling GetRatingByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenRatingIdMissing() {
    // Arrange
    when(ratingRepository.findById(5L)).thenReturn(Optional.empty());

    // Act
    Optional<Rating> result = ratingQueryService.handle(new GetRatingByIdQuery(5L));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a technician with ratings, when handling GetRatingsByTechnicianIdQuery, then it returns them")
  void handle_ShouldReturnRatingsByTechnician_WhenTechnicianHasRatings() {
    // Arrange
    List<Rating> ratings = List.of(mock(Rating.class));
    when(ratingRepository.findByTechnicianId(new TechnicianId(7L))).thenReturn(ratings);

    // Act
    List<Rating> result = ratingQueryService.handle(new GetRatingsByTechnicianIdQuery(7L));

    // Assert
    assertEquals(ratings, result);
  }
}
