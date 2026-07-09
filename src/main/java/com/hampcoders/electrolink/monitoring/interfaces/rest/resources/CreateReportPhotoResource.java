package com.hampcoders.electrolink.monitoring.interfaces.rest.resources;

import com.hampcoders.electrolink.monitoring.domain.model.commands.ReportPhotoPayloadSupport;
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
) {

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CreateReportPhotoResource that)) {
      return false;
    }
    return ReportPhotoPayloadSupport.payloadEquals(reportId, photoData, fileName, contentType,
        that.reportId, that.photoData, that.fileName, that.contentType);
  }

  @Override
  public int hashCode() {
    return ReportPhotoPayloadSupport.payloadHashCode(reportId, photoData, fileName, contentType);
  }

  @Override
  public String toString() {
    return ReportPhotoPayloadSupport.payloadToString("CreateReportPhotoResource",
        reportId, photoData, fileName, contentType);
  }
}
