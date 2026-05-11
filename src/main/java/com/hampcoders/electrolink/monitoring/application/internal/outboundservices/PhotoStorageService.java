package com.hampcoders.electrolink.monitoring.application.internal.outboundservices;

public interface PhotoStorageService {
    String storePhoto(byte[] photoData, String fileName, String contentType);
    void deletePhoto(String photoUrl);
}
