package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.entities.Bill;
import com.hampcoders.electrolink.sdp.domain.model.entities.Photo;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateRequestResource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert CreateRequestResource to Request domain model.
 */
public class RequestMapper {

  /**
   * Converts a {@link CreateRequestResource} to a {@link Request} domain model.
   *
   * @param resource The resource to convert.
   * @return The converted {@link Request} model.
   */
  public static Request toModel(CreateRequestResource resource) {
    Bill bill = new Bill(
        resource.bill().billingPeriod(),
        resource.bill().energyConsumed(),
        resource.bill().amountPaid(),
        resource.bill().billImageUrl()
    );

    List<Photo> photos = resource.photos() != null
        ? resource.photos().stream()
        .map(photo -> new Photo(photo.photoId(), photo.url()))
        .collect(Collectors.toList())
        : List.of();

    return new Request(
        resource.clientId(),
        resource.technicianId(),
        resource.propertyId(),
        resource.serviceId(),
        resource.problemDescription(),
        resource.scheduledDate(),
        bill,
        photos,
        resource.isPriority()
    );
  }
}
