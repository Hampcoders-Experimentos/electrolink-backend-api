package com.hampcoders.electrolink.monitoring.infrastructure.storage.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hampcoders.electrolink.monitoring.application.internal.outboundservices.PhotoStorageService;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for storing photos in Cloudinary.
 */
@Service
@ConditionalOnProperty(name = "monitoring.photos.storage.provider", havingValue = "cloudinary")
public class CloudinaryPhotoStorageService implements PhotoStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CloudinaryPhotoStorageService.class);
  private static final String DEFAULT_FOLDER = "monitoring/photos";
  private static final Pattern VERSION_PATTERN = Pattern.compile("/v\\d+/");

  private final Cloudinary cloudinary;

  /**
   * Constructor for CloudinaryPhotoStorageService.
   *
   * @param cloudName the Cloudinary cloud name, injected from application properties
   * @param apiKey the Cloudinary API key, injected from application properties
   * @param apiSecret the Cloudinary API secret, injected from application properties
   */
  public CloudinaryPhotoStorageService(
      @Value("${cloudinary.cloud-name}") String cloudName,
      @Value("${cloudinary.api-key}") String apiKey,
      @Value("${cloudinary.api-secret}") String apiSecret) {
    this.cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", cloudName,
        "api_key", apiKey,
        "api_secret", apiSecret
    ));
  }

  @Override
  public String storePhoto(byte[] photoData, String fileName, String contentType) {
    String publicId = UUID.randomUUID().toString();
    try {
      Map<String, Object> params = ObjectUtils.asMap(
          "public_id", publicId,
          "folder", DEFAULT_FOLDER,
          "resource_type", "image"
      );
      Map<?, ?> result = cloudinary.uploader().upload(photoData, params);
      String url = (String) result.get("secure_url");
      LOGGER.info("Photo uploaded to Cloudinary: {}", url);
      return url;
    } catch (IOException e) {
      throw new RuntimeException("Failed to upload photo to Cloudinary", e);
    }
  }

  @Override
  public void deletePhoto(String photoUrl) {
    try {
      String publicId = extractPublicId(photoUrl);
      Map<String, Object> params = ObjectUtils.asMap("resource_type", "image");
      Map<?, ?> result = cloudinary.uploader().destroy(publicId, params);
      String status = (String) result.get("result");
      LOGGER.info("Photo deleted from Cloudinary. Public ID: {}, Status: {}", publicId, status);
    } catch (IOException e) {
      LOGGER.error("Failed to delete photo from Cloudinary: {}", photoUrl, e);
    }
  }

  private String extractPublicId(String photoUrl) {
    Matcher matcher = VERSION_PATTERN.matcher(photoUrl);
    if (matcher.find()) {
      String afterVersion = photoUrl.substring(matcher.end());
      int lastDot = afterVersion.lastIndexOf('.');
      if (lastDot != -1) {
        return afterVersion.substring(0, lastDot);
      }
      return afterVersion;
    }
    throw new IllegalArgumentException("Cannot extract public ID from Cloudinary URL: " + photoUrl);
  }
}
