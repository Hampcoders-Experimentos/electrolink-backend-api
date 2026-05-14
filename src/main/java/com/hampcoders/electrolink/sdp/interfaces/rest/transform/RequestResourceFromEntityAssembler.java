package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.BillResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.PhotoResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.RequestResource;
import java.util.List;

/**
 * Assembles a RequestResource from a Request entity.
 */
public class RequestResourceFromEntityAssembler {

  /**
   * Converts a Request entity to a RequestResource.
   *
   * @param entity the Request entity to convert
   * @return the corresponding RequestResource
   */
  public static RequestResource toResourceFromEntity(final Request entity) {
    var bill = new BillResource(
        entity.getBill().getBillingPeriod(),
        entity.getBill().getEnergyConsumed(),
        entity.getBill().getAmountPaid(),
        entity.getBill().getBillImageUrl());
    var photos = entity.getPhotos().stream()
        .map(p -> new PhotoResource(p.getPhotoId(), p.getUrl()))
        .toList();
    return new RequestResource(
        entity.getId(), entity.getClientId(), entity.getTechnicianId(),
        entity.getPropertyId(), entity.getServiceId(),
        entity.getProblemDescription(), entity.getScheduledDate(),
        entity.isPriority(), bill, photos);
  }
}
