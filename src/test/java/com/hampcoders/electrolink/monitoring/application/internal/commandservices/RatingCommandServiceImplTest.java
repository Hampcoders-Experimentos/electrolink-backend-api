package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Rating;
import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddRatingCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.RatingRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import com.hampcoders.electrolink.shared.test.util.ReflectionTestUtils;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RatingCommandServiceImplTest {

  @Mock
  private RatingRepository ratingRepository;
  @Mock
  private ServiceOperationRepository serviceOperationRepository;

  @InjectMocks
  private RatingCommandServiceImpl ratingCommandService;

  private static AddRatingCommand command() {
    return new AddRatingCommand(new RequestId(1L), 4, "Good", "rater-1", new TechnicianId(2L));
  }

  @Test
  @DisplayName("Given a completed service operation, when handling AddRatingCommand, then it returns the rating id")
  void handle_ShouldReturnRatingId_WhenServiceCompleted() {
    // Arrange
    ServiceOperation serviceOperation = mock(ServiceOperation.class);
    when(serviceOperationRepository.findByRequestId(new RequestId(1L)))
        .thenReturn(List.of(serviceOperation));
    when(serviceOperation.getStatus()).thenReturn(ServiceStatus.COMPLETED);
    when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
      Rating rating = invocation.getArgument(0);
      ReflectionTestUtils.setField(rating, "id", 50L);
      return rating;
    });

    // Act
    Long result = ratingCommandService.handle(command());

    // Assert
    assertEquals(50L, result);
  }

  @Test
  @DisplayName("Given no service operation, when handling AddRatingCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenServiceOperationNotFound() {
    // Arrange
    when(serviceOperationRepository.findByRequestId(new RequestId(1L)))
        .thenReturn(List.of());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> ratingCommandService.handle(command()));
  }

  @Test
  @DisplayName("Given a non-completed service operation, when handling AddRatingCommand, then it throws IllegalState")
  void handle_ShouldThrow_WhenServiceNotCompleted() {
    // Arrange
    ServiceOperation serviceOperation = mock(ServiceOperation.class);
    when(serviceOperationRepository.findByRequestId(new RequestId(1L)))
        .thenReturn(List.of(serviceOperation));
    when(serviceOperation.getStatus()).thenReturn(ServiceStatus.PENDING);

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> ratingCommandService.handle(command()));
  }
}
