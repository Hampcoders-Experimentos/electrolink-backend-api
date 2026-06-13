package com.hampcoders.electrolink.monitoring.domain.model.commands;

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
    return ReportPhotoPayloadSupport.payloadEquals(reportId, photoData, fileName, contentType,
        that.reportId, that.photoData, that.fileName, that.contentType);
  }

  @Override
  public int hashCode() {
    return ReportPhotoPayloadSupport.payloadHashCode(reportId, photoData, fileName, contentType);
  }

  @Override
  public String toString() {
    return ReportPhotoPayloadSupport.payloadToString("AddPhotoCommand",
        reportId, photoData, fileName, contentType);
  }
}
