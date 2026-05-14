package com.hampcoders.electrolink.sdp.domain.model.commands;

import com.hampcoders.electrolink.sdp.domain.model.entities.Bill;
import com.hampcoders.electrolink.sdp.domain.model.entities.Photo;
import java.time.LocalDate;
import java.util.List;

/**
 * Command object representing the data needed to update an existing service request.
 *
 * @param requestId the ID of the request to be updated
 * @param clientId the ID of the client making the request
 * @param technicianId the ID of the technician assigned to the request
 * @param propertyId the ID of the property where the service is to be performed
 * @param serviceId the ID of the service being requested
 * @param problemDescription a description of the problem to be addressed
 * @param scheduledDate the date when the service is scheduled to be performed
 * @param bill the bill associated with the request, if any
 * @param photos a list of photos related to the request, if any
 * @param isPriority a flag indicating whether the request is marked as a priority
 */
public record UpdateRequestCommand(
    Long requestId,
    String clientId,
    String technicianId,
    String propertyId,
    String serviceId,
    String problemDescription,
    LocalDate scheduledDate,
    Bill bill,
    List<Photo> photos,
    boolean isPriority
) {}
