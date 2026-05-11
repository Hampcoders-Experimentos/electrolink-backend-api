package com.hampcoders.electrolink.monitoring.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resource used to represent the data required to associate a new photo with a report.
 *
 * @param reportId    The ID of the report the photo belongs to.
 * @param photoData   The raw byte data of the photo.
 * @param fileName    The original file name of the photo.
 * @param contentType The MIME content type of the photo.
 */
public record CreateReportPhotoResource(
                                         @NotNull Long reportId,
                                         @NotNull byte[] photoData,
                                         @NotBlank String fileName,
                                         @NotBlank String contentType
) {}