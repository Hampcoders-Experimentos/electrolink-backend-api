package com.hampcoders.electrolink.analytics.domain.services;

import com.hampcoders.electrolink.analytics.domain.model.queries.GetHomeOwnerConsumptionQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianPerformanceQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianRevenueQuery;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.HomeOwnerConsumptionResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianPerformanceResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianRevenueResource;
import java.util.List;

public interface AnalyticsQueryService {
    List<HomeOwnerConsumptionResource> handle(GetHomeOwnerConsumptionQuery query);
    List<TechnicianPerformanceResource> handle(GetTechnicianPerformanceQuery query);
    List<TechnicianRevenueResource> handle(GetTechnicianRevenueQuery query);
}
