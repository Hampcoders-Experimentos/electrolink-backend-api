package com.hampcoders.electrolink.analytics.domain.model.queries;

/**
 * Query object representing the data needed to retrieve the energy consumption
 * for a homeowner over a specified number of months.
 *
 * @param ownerId The ID of the homeowner whose energy consumption is being queried.
 * @param months The number of months for which to retrieve the energy consumption data.
 */
public record GetHomeOwnerConsumptionQuery(Long ownerId, int months) {
}
