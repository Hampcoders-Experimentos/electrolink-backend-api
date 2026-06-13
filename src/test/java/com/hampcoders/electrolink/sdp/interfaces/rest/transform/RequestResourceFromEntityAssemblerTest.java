package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.entities.Bill;
import com.hampcoders.electrolink.sdp.domain.model.entities.Photo;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.RequestResource;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestResourceFromEntityAssemblerTest {

  private static Bill bill() {
    Bill bill = mock(Bill.class);
    when(bill.getBillingPeriod()).thenReturn("2026-01");
    when(bill.getEnergyConsumed()).thenReturn(100.0);
    when(bill.getAmountPaid()).thenReturn(50.0);
    when(bill.getBillImageUrl()).thenReturn("http://bill");
    return bill;
  }

  @Test
  @DisplayName("Given a request with photos, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenRequestProvided() {
    // Arrange
    Photo photo = mock(Photo.class);
    when(photo.getPhotoId()).thenReturn("p1");
    when(photo.getUrl()).thenReturn("http://photo");
    Request request = mock(Request.class);
    when(request.getId()).thenReturn(7L);
    when(request.getClientId()).thenReturn("1");
    when(request.getTechnicianId()).thenReturn("99");
    when(request.getPropertyId()).thenReturn("prop-1");
    when(request.getServiceId()).thenReturn("svc-1");
    when(request.getProblemDescription()).thenReturn("desc");
    when(request.getScheduledDate()).thenReturn(LocalDate.now());
    when(request.isPriority()).thenReturn(true);
    when(request.getBill()).thenReturn(bill());
    when(request.getPhotos()).thenReturn(List.of(photo));

    // Act
    RequestResource resource = RequestResourceFromEntityAssembler.toResourceFromEntity(request);

    // Assert
    assertEquals(7L, resource.id());
    assertEquals("1", resource.clientId());
    assertEquals(100.0, resource.bill().energyConsumed());
    assertEquals(1, resource.photos().size());
    assertEquals("p1", resource.photos().get(0).photoId());
  }

  @Test
  @DisplayName("Given a request without photos, when assembling, then the photo list is empty")
  void handle_ShouldMapEmptyPhotos_WhenNoPhotos() {
    // Arrange
    Request request = mock(Request.class);
    when(request.getId()).thenReturn(8L);
    when(request.getClientId()).thenReturn("2");
    when(request.getTechnicianId()).thenReturn("88");
    when(request.getPropertyId()).thenReturn("prop-2");
    when(request.getServiceId()).thenReturn("svc-2");
    when(request.getProblemDescription()).thenReturn("desc2");
    when(request.getScheduledDate()).thenReturn(LocalDate.now());
    when(request.isPriority()).thenReturn(false);
    when(request.getBill()).thenReturn(bill());
    when(request.getPhotos()).thenReturn(List.of());

    // Act
    RequestResource resource = RequestResourceFromEntityAssembler.toResourceFromEntity(request);

    // Assert
    assertTrue(resource.photos().isEmpty());
  }

  @Test
  @DisplayName("Given a null request, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenRequestIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> RequestResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
