package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.CreateReportPhotoResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateReportPhotoCommandFromResourceAssemblerTest {
    @Test
    @DisplayName("toCommandFromResource should correctly map CreateReportPhotoResource to AddPhotoCommand (AAA)")
    void toCommandFromResource_ShouldMapResourceToCommand() {
        // Arrange
        Long reportId = 42L;
        byte[] photoData = new byte[]{1, 2, 3};
        String fileName = "photo_xyz.jpg";
        String contentType = "image/jpeg";

        var resource = new CreateReportPhotoResource(reportId, photoData, fileName, contentType);

        // Act
        var command = CreateReportPhotoCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertNotNull(command, "El comando retornado no debe ser nulo.");
        assertEquals(reportId, command.reportId(),
                "El ReportId debe ser mapeado y envuelto en un Value Object.");
        assertArrayEquals(photoData, command.photoData(), "El photoData debe coincidir.");
        assertEquals(fileName, command.fileName(), "El fileName debe coincidir.");
        assertEquals(contentType, command.contentType(), "El contentType debe coincidir.");
    }
}
