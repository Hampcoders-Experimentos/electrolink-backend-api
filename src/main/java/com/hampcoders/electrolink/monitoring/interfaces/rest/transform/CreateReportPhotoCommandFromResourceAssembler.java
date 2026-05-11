package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import com.hampcoders.electrolink.monitoring.domain.model.commands.AddPhotoCommand;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.CreateReportPhotoResource;

/**
 * Assembler responsible for converting {@link CreateReportPhotoResource} objects
 * into {@link AddPhotoCommand} objects.
 */
public class CreateReportPhotoCommandFromResourceAssembler {

  /**
   * Converts a CreateReportPhotoResource into an AddPhotoCommand.
   *
   * @param resource The resource object containing report photo creation data.
   * @return The corresponding AddPhotoCommand.
   */
  public static AddPhotoCommand toCommandFromResource(CreateReportPhotoResource resource) {
    return new AddPhotoCommand(
        resource.reportId(),
        resource.photoData(),
        resource.fileName(),
        resource.contentType()
    );
  }
}