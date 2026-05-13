package com.hampcoders.electrolink.monitoring.interfaces.rest;

import com.hampcoders.electrolink.monitoring.domain.model.commands.DeleteRatingCommand;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetAllRatingsQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingByIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingsByRequestIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetRatingsByTechnicianIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetFeaturedRatingsByTechnicianIdQuery;
import com.hampcoders.electrolink.monitoring.domain.services.RatingCommandService;
import com.hampcoders.electrolink.monitoring.domain.services.RatingQueryService;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.CreateRatingResource;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.RatingResource;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.UpdateRatingResource;
import com.hampcoders.electrolink.monitoring.interfaces.rest.transform.CreateRatingCommandFromResourceAssembler;
import com.hampcoders.electrolink.monitoring.interfaces.rest.transform.RatingResourceFromEntityAssembler;
import com.hampcoders.electrolink.monitoring.interfaces.rest.transform.UpdateRatingCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing Ratings, providing endpoints for creation,
 * retrieval, updating, and deletion of rating entities.
 */
@Tag(name = "Ratings", description = "Rating Management Endpoints")
@RestController
@RequestMapping("/api/v1/ratings")
public class RatingsController {

  private final RatingCommandService commandService;
  private final RatingQueryService queryService;

  public RatingsController(RatingCommandService commandService,
                           RatingQueryService queryService) {
    this.commandService = commandService;
    this.queryService = queryService;
  }

  @PostMapping
  public ResponseEntity<RatingResource> createRating(@RequestBody CreateRatingResource resource) {
    var command = CreateRatingCommandFromResourceAssembler.toCommandFromResource(resource);
    var ratingId = commandService.handle(command);

    var getQuery = new GetRatingByIdQuery(ratingId);
    var rating = queryService.handle(getQuery)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Rating not found after creation"));

    return new ResponseEntity<>(RatingResourceFromEntityAssembler.toResourceFromEntity(rating),
        HttpStatus.CREATED);
  }

  /**
   * Updates the score and/or comment of an existing rating.
   *
   * @param resource The data containing the rating ID and updated fields.
   * @return Empty response with HTTP status 200 (OK).
   */
  @Operation(summary = "Update a rating", responses = {
      @ApiResponse(responseCode = "200", description = "Rating updated successfully"),
      @ApiResponse(responseCode = "404", description = "Rating not found", content = @Content)
  })
  @PutMapping
  public ResponseEntity<Void> updateRating(@RequestBody UpdateRatingResource resource) {
    var command = UpdateRatingCommandFromResourceAssembler.toCommandFromResource(resource);
    commandService.handle(command);
    return ResponseEntity.ok().build();
  }

  /**
   * Deletes a rating by its unique ID.
   *
   * @param ratingId The ID of the rating to delete.
   * @return Empty response with HTTP status 204 (No Content).
   */
  @Operation(summary = "Delete a rating", responses = {
      @ApiResponse(responseCode = "204", description = "Rating deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Rating not found", content = @Content)
  })
  @DeleteMapping("/{ratingId}")
  public ResponseEntity<Void> deleteRating(
      @Parameter(description = "Id of the rating") @PathVariable Long ratingId
  ) {
    var command = new DeleteRatingCommand(ratingId);
    commandService.handle(command);
    return ResponseEntity.noContent().build();
  }

  /**
   * Retrieves a specific rating by its unique ID.
   *
   * @param ratingId The ID of the rating to retrieve.
   * @return The requested rating resource with HTTP status 200 (OK).
   */
  @Operation(summary = "Get rating by ID", responses = {
      @ApiResponse(responseCode = "200", description = "Found rating",
          content = @Content(schema = @Schema(implementation = RatingResource.class))),
      @ApiResponse(responseCode = "404", description = "Rating not found", content = @Content)
  })
  @GetMapping("/{ratingId}")
  public ResponseEntity<RatingResource> getRatingById(
      @Parameter(description = "Id of the rating") @PathVariable Long ratingId
  ) {
    var query = new GetRatingByIdQuery(ratingId);
    var rating = queryService.handle(query)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found"));

    return ResponseEntity.ok(RatingResourceFromEntityAssembler.toResourceFromEntity(rating));
  }

  /**
   * Retrieves all ratings available in the system.
   *
   * @return A list of all rating resources with HTTP status 200 (OK).
   */
  @Operation(summary = "Get all ratings")
  @GetMapping
  public ResponseEntity<List<RatingResource>> getAllRatings() {
    var ratings = queryService.handle(new GetAllRatingsQuery());
    var resources = ratings.stream()
        .map(RatingResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return ResponseEntity.ok(resources);
  }

  /**
   * Retrieves all ratings given to a specific technician.
   *
   * @param technicianId The ID of the technician.
   * @return A list of rating resources for the technician with HTTP status 200 (OK).
   */
  @Operation(summary = "Get ratings by technician ID")
  @GetMapping("/technicians/{technicianId}")
  public ResponseEntity<List<RatingResource>> getRatingsByTechnicianId(
      @Parameter(description = "Technician ID") @PathVariable Long technicianId
  ) {
    var ratings = queryService.handle(new GetRatingsByTechnicianIdQuery(technicianId));
    var resources = ratings.stream()
        .map(RatingResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return ResponseEntity.ok(resources);
  }

  /**
   * Retrieves all featured ratings for a specific technician.
   *
   * @param technicianId The ID of the technician.
   * @return A list of featured rating resources for the technician.
   */
  @Operation(summary = "Get featured ratings by technician ID")
  @GetMapping("/technicians/{technicianId}/featured")
  public ResponseEntity<List<RatingResource>> getFeaturedRatingsByTechnicianId(
      @Parameter(description = "Technician ID") @PathVariable Long technicianId
  ) {
    var query = new GetFeaturedRatingsByTechnicianIdQuery(technicianId);
    var ratings = queryService.handle(query);
    var resources = ratings.stream()
        .map(RatingResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return ResponseEntity.ok(resources);
  }

  /**
   * Retrieves all ratings associated with a specific service operation request ID.
   *
   * @param requestId The ID of the service operation request.
   * @return A list of rating resources for the request with HTTP status 200 (OK).
   */
  @Operation(summary = "Get ratings by request ID")
  @GetMapping("/requests/{requestId}")
  public ResponseEntity<List<RatingResource>> getRatingsByRequestId(
      @Parameter(description = "Request ID") @PathVariable Long requestId
  ) {
    var ratings = queryService.handle(new GetRatingsByRequestIdQuery(requestId));
    var resources = ratings.stream()
        .map(RatingResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return ResponseEntity.ok(resources);
  }
}
