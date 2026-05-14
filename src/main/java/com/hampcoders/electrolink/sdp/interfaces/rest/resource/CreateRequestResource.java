package com.hampcoders.electrolink.sdp.interfaces.rest.resource;

import java.time.LocalDate;
import java.util.List;

/**
 * Resource class representing the data needed to create a new service request.
 *
 * @param clientId The ID of the client making the request.
 * @param technicianId The ID of the technician assigned to the request.
 * @param propertyId The ID of the property where the service is to be performed.
 * @param serviceId The ID of the service being requested.
 * @param problemDescription A description of the problem that needs to be addressed.
 * @param scheduledDate The date when the service is scheduled to be performed.
 * @param bill The billing information associated with the service request.
 * @param photos A list of photos related to the service request, if any.
 * @param isPriority A flag indicating whether the service request is a priority.
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
) {}
