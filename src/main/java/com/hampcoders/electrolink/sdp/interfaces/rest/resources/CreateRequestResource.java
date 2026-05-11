package com.hampcoders.electrolink.sdp.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

/**
 * Resource representing a request to create a service request.
 *
 * @param clientId          The ID of the client making the request.
 * @param technicianId      The ID of the technician assigned to the request.
 * @param propertyId        The ID of the property where the service is requested.
 * @param serviceId         The ID of the service being requested.
 * @param problemDescription A description of the problem to be addressed.
 * @param scheduledDate     The date when the service is scheduled.
 * @param bill              The billing information associated with the request.
 * @param photos            A list of photos related to the request.
 */
public record CreateRequestResource(
    String clientId,
    String technicianId,
    String propertyId,
    String serviceId,
    String problemDescription,
    LocalDate scheduledDate,
    BillResource bill,
    List<PhotoResource> photos,
    boolean isPriority
) {
  /**
   * Resource representing billing information for a service request.
   *
   * @param billingPeriod  The billing period (e.g., "2023-01").
   *
   * @param energyConsumed The amount of energy consumed.
   *
   * @param amountPaid     The amount paid for the bill.
   *
   * @param billImageUrl   The URL of the bill image.
   *
   */
  public record BillResource(
      String billingPeriod,
      double energyConsumed,
      double amountPaid,
      String billImageUrl
  ) {}

  /**
   * Resource representing a photo associated with a service request.
   *
   * @param photoId The unique identifier of the photo.
   *
   * @param url     The URL of the photo.
   */
  public record PhotoResource(
      String photoId,
      String url
  ) {}
}
