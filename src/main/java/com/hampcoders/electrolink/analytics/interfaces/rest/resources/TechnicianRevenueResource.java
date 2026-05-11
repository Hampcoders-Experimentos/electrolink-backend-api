package com.hampcoders.electrolink.analytics.interfaces.rest.resources;

public record TechnicianRevenueResource(
    Long technicianId,
    String period,
    double totalRevenue,
    int servicesCount,
    double averageRevenuePerService
) {
}
