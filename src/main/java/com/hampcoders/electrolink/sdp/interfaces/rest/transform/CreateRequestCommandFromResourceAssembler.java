package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.commands.CreateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.entities.Bill;
import com.hampcoders.electrolink.sdp.domain.model.entities.Photo;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.CreateRequestResource;
import java.util.List;

public class CreateRequestCommandFromResourceAssembler {
  public static CreateRequestCommand toCommandFromResource(final CreateRequestResource resource) {
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
    return new CreateRequestCommand(
        resource.clientId(), resource.technicianId(),
        resource.propertyId(), resource.serviceId(),
        resource.problemDescription(), resource.scheduledDate(),
        bill, photos, resource.isPriority());
  }
}
