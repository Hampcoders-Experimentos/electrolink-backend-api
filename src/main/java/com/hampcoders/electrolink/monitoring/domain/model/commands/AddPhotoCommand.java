package com.hampcoders.electrolink.monitoring.domain.model.commands;

/**
 * Command to add a new photo to an existing report.
 *
 * @param reportId    The ID of the report the photo belongs to.
 * @param photoData   The raw byte data of the photo.
 * @param fileName    The original file name of the photo.
 * @param contentType The MIME content type of the photo.
 */
public record AddPhotoCommand(Long reportId, byte[] photoData, String fileName, String contentType) {

}