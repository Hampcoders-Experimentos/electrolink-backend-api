package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.monitoring.domain.model.commands.AddPhotoCommand;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.CreateReportPhotoResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateReportPhotoCommandFromResourceAssemblerTest {

  @Test
  @DisplayName("Given a resource, when assembling, then it maps all photo fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    byte[] data = {1, 2, 3};
    CreateReportPhotoResource resource =
        new CreateReportPhotoResource(1L, data, "photo.png", "image/png");

    // Act
    AddPhotoCommand command =
        CreateReportPhotoCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(1L, command.reportId());
    assertArrayEquals(data, command.photoData());
    assertEquals("photo.png", command.fileName());
    assertEquals("image/png", command.contentType());
  }

  @Test
  @DisplayName("Given different values, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentResourceProvided() {
    // Arrange
    byte[] data = {9, 9};
    CreateReportPhotoResource resource =
        new CreateReportPhotoResource(7L, data, "img.jpg", "image/jpeg");

    // Act
    AddPhotoCommand command =
        CreateReportPhotoCommandFromResourceAssembler.toCommandFromResource(resource);

    // Assert
    assertEquals(7L, command.reportId());
    assertEquals("img.jpg", command.fileName());
    assertEquals("image/jpeg", command.contentType());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreateReportPhotoCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
