package com.hampcoders.electrolink.sdp.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

/**
 * Resource class representing a service request in the REST API.
 *
 * @param id The unique identifier of the request.
 * @param clientId The ID of the client who made the request.
 * @param technicianId The ID of the technician assigned to the request.
 * @param propertyId The ID of the property where the service is to be performed.
 * @param serviceId The ID of the service being requested.
 * @param problemDescription A description of the problem or issue that needs to be addressed.
 * @param scheduledDate The date when the service is scheduled to be performed.
 * @param isPriority A flag indicating whether the request is a priority.
 * @param bill The billing information associated with the request.
 * @param photos A list of photos related to the request,
 *      such as images of the problem or the property.
 */
public record RequestResource(
    Long id,
    String clientId,
    String technicianId,
    String propertyId,
    String serviceId,
    String problemDescription,
    LocalDate scheduledDate,
    boolean isPriority,
    BillResource bill,
    List<PhotoResource> photos
) {}
