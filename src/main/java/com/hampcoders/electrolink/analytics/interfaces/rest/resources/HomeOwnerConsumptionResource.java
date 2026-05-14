package com.hampcoders.electrolink.analytics.interfaces.rest.resources;

/**
 * Resource class representing the consumption data for a homeowner.
 *
 * @param ownerId The unique identifier of the homeowner.
 * @param month The month for which the consumption data is relevant.
 * @param year The year for which the consumption data is relevant.
 * @param energyConsumed The total energy consumed by the homeowner in the specified month and year.
 * @param amountPaid The total amount paid by the homeowner for
 *     the energy consumed in the specified month and year.
 * @param serviceRequestsCount The total number of service requests made by
 *     the homeowner in the specified month and year.
 */
public record HomeOwnerConsumptionResource(
    Long ownerId,
    int month,
    int year,
    double energyConsumed,
    double amountPaid,
    int serviceRequestsCount
) {
}
