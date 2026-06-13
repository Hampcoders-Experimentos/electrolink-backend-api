package com.hampcoders.electrolink.monitoring.domain.model.commands;

import java.util.Arrays;
import java.util.Objects;

/**
 * Command to add a new photo to an existing report.
 *
 * @param reportId    The ID of the report the photo belongs to.
 * @param photoData   The raw byte data of the photo.
 * @param fileName    The original file name of the photo.
 * @param contentType The MIME content type of the photo.
 */
public record AddPhotoCommand(Long reportId, byte[] photoData,
                              String fileName, String contentType) {

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AddPhotoCommand that)) {
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
    return "AddPhotoCommand{"
        + "reportId=" + reportId
        + ", photoData=" + Arrays.toString(photoData)
        + ", fileName='" + fileName + '\''
        + ", contentType='" + contentType + '\''
        + '}';
  }
}