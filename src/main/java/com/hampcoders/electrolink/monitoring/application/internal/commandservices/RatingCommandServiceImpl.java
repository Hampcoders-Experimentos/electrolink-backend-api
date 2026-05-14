package com.hampcoders.electrolink.monitoring.application.internal.commandservices;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Rating;
import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.commands.AddRatingCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.DeleteRatingCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.UpdateRatingCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.services.RatingCommandService;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.RatingRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the command service for Rating entities.
 */
@Service
public class RatingCommandServiceImpl implements RatingCommandService {

  private final RatingRepository ratingRepository;
  private final ServiceOperationRepository serviceOperationRepository;

  /**
   * Constructor for RatingCommandServiceImpl.
   *
   * @param ratingRepository Repository for managing Rating entities.
   * @param serviceOperationRepository Repository for managing ServiceOperation entities.
   */
  public RatingCommandServiceImpl(RatingRepository ratingRepository,
                                  ServiceOperationRepository serviceOperationRepository) {
    this.ratingRepository = ratingRepository;
    this.serviceOperationRepository = serviceOperationRepository;
  }

  @Override
  @Transactional
  public Long handle(AddRatingCommand command) {

    ServiceOperation serviceOperation = serviceOperationRepository
        .findByRequestId(command.requestId())
        .orElseThrow(() -> new IllegalArgumentException(
            "No ServiceOperation found for the given RequestId"));

    if (serviceOperation.getStatus() != ServiceStatus.COMPLETED) {
      throw new IllegalStateException(
          "Cannot add rating: associated ServiceOperation is not completed.");
    }

    var rating = new Rating(command);

    // Automatically set isFeatured based on score
    if (rating.getScore() == 5) {
      rating.feature();
    } else {
      rating.unfeature();
    }

    ratingRepository.save(rating);
    return rating.getId();
  }

  @Override
  @Transactional
  public void handle(UpdateRatingCommand command) {
    var rating = ratingRepository.findById(command.ratingId())
        .orElseThrow(() -> new IllegalArgumentException("Rating not found"));


    if (command.score() != null) {
      rating.updateScore(command.score());
    }

    if (command.comment() != null) {
      rating.updateComment(command.comment());
    }

    if (rating.getScore() == 5) {
      rating.feature();
    } else {
      rating.unfeature();
    }
    
    ratingRepository.save(rating);
  }

  @Override
  @Transactional
  public void handle(DeleteRatingCommand command) {
    var rating = ratingRepository.findById(command.ratingId())
        .orElseThrow(() -> new IllegalArgumentException("Rating not found"));
    ratingRepository.delete(rating);
  }
}
