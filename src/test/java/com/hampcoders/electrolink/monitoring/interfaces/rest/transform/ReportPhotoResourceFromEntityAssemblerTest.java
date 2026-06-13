package com.hampcoders.electrolink.monitoring.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.monitoring.domain.model.entities.ReportPhoto;
import com.hampcoders.electrolink.monitoring.interfaces.rest.resources.ReportPhotoResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReportPhotoResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a report photo, when assembling, then it maps id, report id and url")
  void handle_ShouldMapAllFields_WhenPhotoProvided() {
    // Arrange
    ReportPhoto photo = mock(ReportPhoto.class);
    when(photo.getId()).thenReturn(9L);
    when(photo.getReportId()).thenReturn(1L);
    when(photo.getUrl()).thenReturn("http://url/photo.png");

    // Act
    ReportPhotoResource resource =
        ReportPhotoResourceFromEntityAssembler.toResourceFromEntity(photo);

    // Assert
    assertEquals(9L, resource.id());
    assertEquals(1L, resource.reportId());
    assertEquals("http://url/photo.png", resource.url());
  }

  @Test
  @DisplayName("Given another report photo, when assembling, then it maps the new values")
  void handle_ShouldMapNewValues_WhenDifferentPhotoProvided() {
    // Arrange
    ReportPhoto photo = mock(ReportPhoto.class);
    when(photo.getId()).thenReturn(10L);
    when(photo.getReportId()).thenReturn(2L);
    when(photo.getUrl()).thenReturn("http://url/other.jpg");

    // Act
    ReportPhotoResource resource =
        ReportPhotoResourceFromEntityAssembler.toResourceFromEntity(photo);

    // Assert
    assertEquals(10L, resource.id());
    assertEquals(2L, resource.reportId());
    assertEquals("http://url/other.jpg", resource.url());
  }

  @Test
  @DisplayName("Given a null report photo, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenPhotoIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> ReportPhotoResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
