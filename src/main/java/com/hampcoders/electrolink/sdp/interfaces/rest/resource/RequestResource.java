package com.hampcoders.electrolink.sdp.interfaces.rest.resource;

import java.time.LocalDate;
import java.util.List;

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
