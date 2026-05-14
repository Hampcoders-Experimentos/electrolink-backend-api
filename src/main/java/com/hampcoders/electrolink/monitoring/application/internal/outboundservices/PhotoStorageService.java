package com.hampcoders.electrolink.monitoring.application.internal.outboundservices;

/**
 * Interface for photo storage service.
 * This service is responsible for storing photos and providing URLs for access.
 */
public interface PhotoStorageService {
  /**
   * Stores a photo and returns the URL for access.
   * The implementation of this method should handle the actual storage of the photo,
   *
   * @param photoData The byte array of the photo to be stored.
   * @param fileName The name of the file to be stored.
   * @param contentType The content type of the photo.
   * @return The URL where the photo can be accessed.
   */
  String storePhoto(byte[] photoData, String fileName, String contentType);

  /**
   * Deletes a photo from storage.
   *
   * @param photoUrl The URL of the photo to be deleted.
   */
  void deletePhoto(String photoUrl);
}
