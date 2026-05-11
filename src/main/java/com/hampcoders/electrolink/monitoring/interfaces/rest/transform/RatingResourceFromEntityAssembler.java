package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Rating;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.RatingResource;

/**
 * Assembler responsible for converting {@link Rating} entities
 * into {@link RatingResource} objects.
 */
public class RatingResourceFromEntityAssembler {

  /**
   * Converts a Rating entity into a RatingResource.
   *
   * @param entity The Rating entity.
   * @return The corresponding RatingResource.
   */
  public static RatingResource toResourceFromEntity(Rating entity) {
    return new RatingResource(
        entity.getId(),
        entity.getRequestId().requestId(),
        entity.getScore(),
        entity.getComment(),
        entity.getRaterId(),
        entity.getTechnicianId().technicianId()
    );
  }
}