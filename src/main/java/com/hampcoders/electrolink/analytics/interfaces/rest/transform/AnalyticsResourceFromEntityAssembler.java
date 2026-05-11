package com.hampcoders.electrolink.analytics.interfaces.rest.transform;

import com.hampcoders.electrolink.analytics.interfaces.rest.resources.HomeOwnerConsumptionResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianPerformanceResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianRevenueResource;
import java.util.List;

public class AnalyticsResourceFromEntityAssembler {

    public static List<HomeOwnerConsumptionResource> toHomeOwnerConsumptionResourceList(
        List<HomeOwnerConsumptionResource> resources) {
        return resources;
    }

    public static TechnicianPerformanceResource toTechnicianPerformanceResource(
        Long technicianId, int totalServicesCompleted, double averageRating,
        double averageCompletionTimeHours, int pendingServices) {
        return new TechnicianPerformanceResource(technicianId, totalServicesCompleted,
            averageRating, averageCompletionTimeHours, pendingServices);
    }

    public static TechnicianRevenueResource toTechnicianRevenueResource(
        Long technicianId, String period, double totalRevenue,
        int servicesCount, double averageRevenuePerService) {
        return new TechnicianRevenueResource(technicianId, period, totalRevenue,
            servicesCount, averageRevenuePerService);
    }
}
