package com.hampcoders.electrolink.monitoring.domain.model.commands;

import java.util.Arrays;
import java.util.Objects;

/**
 * Shared helper for the array-aware {@code equals}, {@code hashCode}, and {@code toString}
 * logic of report-photo payloads (report id, photo bytes, file name and content type).
 *
 * <p>Both {@code AddPhotoCommand} and {@code CreateReportPhotoResource} expose the same set of
 * fields, including a {@code byte[]} that must be compared by content; centralizing the logic here
 * avoids duplicating it in every payload type.</p>
 */
public final class ReportPhotoPayloadSupport {

  private ReportPhotoPayloadSupport() {
  }

  /**
   * Compares two report-photo payloads by content, using {@link Arrays#equals} for the photo bytes.
   *
   * @return {@code true} if every field is equal
   */
  public static boolean payloadEquals(Long reportId, byte[] photoData, String fileName,
                                      String contentType, Long otherReportId, byte[] otherPhotoData,
                                      String otherFileName, String otherContentType) {
    return Objects.equals(reportId, otherReportId)
        && Arrays.equals(photoData, otherPhotoData)
        && Objects.equals(fileName, otherFileName)
        && Objects.equals(contentType, otherContentType);
  }

  /**
   * Computes a content-based hash code, using {@link Arrays#hashCode} for the photo bytes.
   */
  public static int payloadHashCode(Long reportId, byte[] photoData, String fileName,
                                    String contentType) {
    int result = Objects.hash(reportId, fileName, contentType);
    return 31 * result + Arrays.hashCode(photoData);
  }

  /**
   * Builds a content-based string representation, using {@link Arrays#toString} for the photo bytes.
   *
   * @param typeName the simple name of the payload type to prefix the representation with
   */
  public static String payloadToString(String typeName, Long reportId, byte[] photoData,
                                       String fileName, String contentType) {
    return typeName + "{"
        + "reportId=" + reportId
        + ", photoData=" + Arrays.toString(photoData)
        + ", fileName='" + fileName + '\''
        + ", contentType='" + contentType + '\''
        + '}';
  }
}
