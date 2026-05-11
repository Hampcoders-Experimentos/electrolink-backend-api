package com.hampcoders.electrolink.analytics.interfaces.rest.resources;

public record HomeOwnerConsumptionResource(
    Long ownerId,
    int month,
    int year,
    double energyConsumed,
    double amountPaid,
    int serviceRequestsCount
) {
}
