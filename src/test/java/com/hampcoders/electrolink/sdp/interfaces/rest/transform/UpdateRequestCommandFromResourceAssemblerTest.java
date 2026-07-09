package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateRequestCommand;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.BillResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateRequestResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.PhotoResource;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateRequestCommandFromResourceAssemblerTest {

  private static BillResource bill() {
    return new BillResource("2026-01", 100.0, 50.0, "http://bill");
  }

  @Test
  @DisplayName("Given a resource and id, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Arrange
    CreateRequestResource resource = new CreateRequestResource(
        "1", "99", "prop-1", "svc-1", "desc", LocalDate.now(), bill(),
        List.of(new PhotoResource("p1", "http://photo")), true);

    // Act
    UpdateRequestCommand command =
        UpdateRequestCommandFromResourceAssembler.toCommandFromResource(7L, resource);

    // Assert
    assertEquals(7L, command.requestId());
    assertEquals("1", command.clientId());
    assertEquals("svc-1", command.serviceId());
    assertEquals(1, command.photos().size());
  }

  @Test
  @DisplayName("Given null photos, when assembling, then it maps an empty photo list")
  void handle_ShouldMapEmptyPhotos_WhenPhotosAreNull() {
    // Arrange
    CreateRequestResource resource = new CreateRequestResource(
        "1", "99", "prop-1", "svc-1", "desc", LocalDate.now(), bill(), null, false);

    // Act
    UpdateRequestCommand command =
        UpdateRequestCommandFromResourceAssembler.toCommandFromResource(7L, resource);

    // Assert
    assertTrue(command.photos().isEmpty());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> UpdateRequestCommandFromResourceAssembler.toCommandFromResource(7L, null));
  }
}
