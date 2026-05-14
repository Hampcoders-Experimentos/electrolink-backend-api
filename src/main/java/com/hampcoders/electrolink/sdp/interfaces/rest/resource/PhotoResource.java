package com.hampcoders.electrolink.sdp.interfaces.rest.resource;

/**
 * Represents a photo resource with its unique identifier and URL.
 *
 * @param photoId The unique identifier for the photo.
 * @param url The URL where the photo can be accessed.
 */
public record PhotoResource(
    String photoId,
    String url
) {}
