package com.hampcoders.electrolink.monitoring.domain.services;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Rating;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetAllRatingsQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetFeaturedRatingsByTechnicianIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingByIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingsByRequestIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingsByTechnicianIdQuery;
import java.util.List;
import java.util.Optional;

/**
 * Defines the query operations available for retrieving Rating entities.
 */
public interface RatingQueryService {

  /**
   * Retrieves all existing ratings.
   *
   * @param query The query object (marker).
   * @return A list of all ratings.
   */
  List<Rating> handle(GetAllRatingsQuery query);

  /**
   * Retrieves a specific rating by its ID.
   *
   * @param query The query object containing the rating ID.
   * @return An Optional containing the rating, or empty if not found.
   */
  Optional<Rating> handle(GetRatingByIdQuery query);

  /**
   * Retrieves all ratings associated with a specific technician ID.
   *
   * @param query The query object containing the technician ID.
   * @return A list of ratings for the specified technician.
   */
  List<Rating> handle(GetRatingsByTechnicianIdQuery query);

  /**
   * Retrieves all featured ratings for a specific technician.
   *
   * @param query The query object containing the technician ID.
   * @return A list of featured ratings for the specified technician.
   */
  List<Rating> handle(GetFeaturedRatingsByTechnicianIdQuery query);

  /**
   * Retrieves all ratings associated with a specific request ID.
   *
   * @param query The query object containing the request ID.
   * @return A list of ratings for the specified request.
   */
  List<Rating> handle(GetRatingsByRequestIdQuery query);
}
