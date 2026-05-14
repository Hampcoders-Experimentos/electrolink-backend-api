package com.hampcoders.electrolink.monitoring.application.internal.queryservices;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Rating;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetAllRatingsQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetFeaturedRatingsByTechnicianIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingByIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingsByRequestIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingsByTechnicianIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.domain.services.RatingQueryService;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.RatingRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the query service for Rating entities.
 */
@Service
public class RatingQueryServiceImpl implements RatingQueryService {

  private final RatingRepository ratingRepository;

  /**
   * Constructor for RatingQueryServiceImpl.
   *
   * @param ratingRepository The repository used to access Rating data from the database.
   */
  public RatingQueryServiceImpl(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  /**
   * Handles the query to retrieve all ratings.
   *
   * @param query The query object (placeholder).
   * @return A list of all Rating entities.
   */
  @Override
  public List<Rating> handle(GetAllRatingsQuery query) {
    return ratingRepository.findAll();
  }

  /**
   * Handles the query to retrieve a rating by its ID.
   *
   * @param query The query object containing the rating ID.
   * @return An Optional containing the Rating entity, or empty if not found.
   */
  @Override
  public Optional<Rating> handle(GetRatingByIdQuery query) {
    return ratingRepository.findById(query.ratingId());
  }

  /**
   * Handles the query to retrieve all ratings associated with a specific request ID.
   *
   * @param query The query object containing the request ID.
   * @return A list of Rating entities matching the request ID.
   */
  @Override
  public List<Rating> handle(GetRatingsByRequestIdQuery query) {
    return ratingRepository.findByRequestId(new RequestId(query.requestId()));
  }

  /**
   * Handles the query to retrieve all ratings given by a specific technician ID.
   *
   * @param query The query object containing the technician ID.
   * @return A list of Rating entities matching the technician ID.
   */
  @Override
  public List<Rating> handle(GetRatingsByTechnicianIdQuery query) {
    return ratingRepository.findByTechnicianId(new TechnicianId(query.technicianId()));
  }

  /**
   * Handles the query to retrieve all featured ratings for a specific technician.
   *
   * @param query The query containing the technician ID.
   * @return A list of featured Rating entities.
   */
  @Override
  public List<Rating> handle(GetFeaturedRatingsByTechnicianIdQuery query) {
    return ratingRepository.findByTechnicianIdAndIsFeatured(
        new TechnicianId(query.technicianId()), true);
  }
}
