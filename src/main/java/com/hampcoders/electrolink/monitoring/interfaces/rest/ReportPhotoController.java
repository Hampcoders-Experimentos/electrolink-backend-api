package com.hampcoders.electrolink.monitoring.interfaces.rest;

import com.hampcoders.electrolink.monitoring.domain.services.ReportPhotoCommandService;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.CreateReportPhotoResource;
import com.hampcoders.electrolink.monitoring.interfaces.rest.transform.CreateReportPhotoCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing report photos, providing endpoints for adding photos to reports.
 */
@Tag(name = "Report Photos", description = "Photo management for reports")
@RestController
@RequestMapping(value = "/api/v1/photos", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportPhotoController {

  private final ReportPhotoCommandService photoCommandService;

  /**
   * Constructs a ReportPhotoController with the necessary command service.
   *
   * @param photoCommandService The service for handling photo commands.
   */
  public ReportPhotoController(ReportPhotoCommandService photoCommandService) {
    this.photoCommandService = photoCommandService;
  }

  /**
   * Endpoint to add a new photo to a report.
   *
   * @param resource The resource containing the report ID and photo URL.
   * @return A ResponseEntity with HTTP status 201 (Created) and the location of the new resource.
   */
  @PostMapping
  public ResponseEntity<?> addPhoto(@RequestBody CreateReportPhotoResource resource) {
    var command = CreateReportPhotoCommandFromResourceAssembler.toCommandFromResource(resource);
    Long photoId = photoCommandService.handle(command);
    return ResponseEntity.created(URI.create("/api/v1/photos/" + photoId)).body(resource);
  }
}