package com.hampcoders.electrolink.monitoring.infrastructure.storage.local;

import com.hampcoders.electrolink.monitoring.application.internal.outboundservices.PhotoStorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service implementation for storing photos on the local file system.
 */
@Service
@ConditionalOnProperty(name = "monitoring.photos.storage.provider",
    havingValue = "local", matchIfMissing = true)
public class LocalFilePhotoStorageService implements PhotoStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalFilePhotoStorageService.class);

  private final Path storageDirectory;

  /**
   * Constructor for LocalFilePhotoStorageService.
   *
   * @param storageDirectoryPath The directory path where photos will be stored,
   *     injected from application properties. Defaults to "uploads/photos" if not specified.
   */
  public LocalFilePhotoStorageService(
      @Value("${monitoring.photos.storage.directory:uploads/photos}") String storageDirectoryPath) {
    this.storageDirectory = Paths.get(storageDirectoryPath).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.storageDirectory);
    } catch (IOException e) {
      throw new RuntimeException("Could not create photo storage directory: "
          + this.storageDirectory, e);
    }
  }

  @Override
  public String storePhoto(byte[] photoData, String fileName, String contentType) {
    String extension = extractExtension(fileName);
    String uniqueFileName = UUID.randomUUID().toString() + extension;
    Path destination = storageDirectory.resolve(uniqueFileName);
    try {
      Files.write(destination, photoData);
      LOGGER.info("Photo stored at: {}", destination);
    } catch (IOException e) {
      throw new RuntimeException("Failed to store photo: " + destination, e);
    }
    return destination.toString();
  }

  @Override
  public void deletePhoto(String photoUrl) {
    try {
      Path filePath = Paths.get(photoUrl);
      boolean deleted = Files.deleteIfExists(filePath);
      if (deleted) {
        LOGGER.info("Photo deleted: {}", photoUrl);
      } else {
        LOGGER.warn("Photo not found for deletion: {}", photoUrl);
      }
    } catch (IOException e) {
      LOGGER.error("Failed to delete photo: {}", photoUrl, e);
    }
  }

  private String extractExtension(String fileName) {
    if (fileName == null || fileName.lastIndexOf('.') == -1) {
      return "";
    }
    return fileName.substring(fileName.lastIndexOf('.'));
  }
}
