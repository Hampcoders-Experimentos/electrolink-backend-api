package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.entities.Bill;
import com.hampcoders.electrolink.sdp.domain.model.entities.Photo;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.CreateRequestResource;
import java.util.List;

/**
 * Assembler class that transforms a CreateRequestResource into an UpdateRequestCommand.
 */
public class UpdateRequestCommandFromResourceAssembler {

  /**
   * Converts a CreateRequestResource into an UpdateRequestCommand, using the provided requestId.
   *
   * @param requestId the ID of the request to be updated
   * @param resource the CreateRequestResource containing the new request data
   * @return an UpdateRequestCommand with the data from the resource and the provided requestId
   */
  public static UpdateRequestCommand toCommandFromResource(
      final Long requestId, final CreateRequestResource resource) {
    var bill = new Bill(
        resource.bill().billingPeriod(),
        resource.bill().energyConsumed(),
        resource.bill().amountPaid(),
        resource.bill().billImageUrl());
    var photos = resource.photos() != null
        ? resource.photos().stream()
            .map(p -> new Photo(p.photoId(), p.url()))
            .toList()
        : List.<Photo>of();
    return new UpdateRequestCommand(
        requestId,
        resource.clientId(), resource.technicianId(),
        resource.propertyId(), resource.serviceId(),
        resource.problemDescription(), resource.scheduledDate(),
        bill, photos, resource.isPriority());
  }
}
