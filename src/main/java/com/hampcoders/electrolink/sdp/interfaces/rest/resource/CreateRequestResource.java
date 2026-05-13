package com.hampcoders.electrolink.sdp.interfaces.rest.resource;

import java.time.LocalDate;
import java.util.List;

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
