package com.hampcoders.electrolink.monitoring.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Objects;

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
    return Objects.equals(reportId, that.reportId)
        && Arrays.equals(photoData, that.photoData)
        && Objects.equals(fileName, that.fileName)
        && Objects.equals(contentType, that.contentType);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(reportId, fileName, contentType);
    result = 31 * result + Arrays.hashCode(photoData);
    return result;
  }

  @Override
  public String toString() {
    return "CreateReportPhotoResource{"
        + "reportId=" + reportId
        + ", photoData=" + Arrays.toString(photoData)
        + ", fileName='" + fileName + '\''
        + ", contentType='" + contentType + '\''
        + '}';
  }
}