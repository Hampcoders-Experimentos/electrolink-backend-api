package com.hampcoders.electrolink.analytics.domain.services;

import com.hampcoders.electrolink.analytics.domain.model.queries.GetHomeOwnerConsumptionQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianPerformanceQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianRevenueQuery;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.HomeOwnerConsumptionResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianPerformanceResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianRevenueResource;
import java.util.List;

/**
 * This interface is responsible for providing the analytics query-related operations.
 */
public interface AnalyticsQueryService {

  /**
   * Handles the query for retrieving homeowner consumption data.
   *
   * @param query The {@link GetHomeOwnerConsumptionQuery}
   * @return a list of {@link HomeOwnerConsumptionResource}
   *     containing the consumption data for homeowners.
   */
  List<HomeOwnerConsumptionResource> handle(GetHomeOwnerConsumptionQuery query);

  /**
   * Handles the query for retrieving technician performance data.
   *
   * @param query the {@link GetTechnicianPerformanceQuery}
   *         containing the query parameters.
   * @return a list of {@link TechnicianPerformanceResource}
   *         containing the performance data for technicians.
   */
  List<TechnicianPerformanceResource> handle(GetTechnicianPerformanceQuery query);

  /**
   * Handles the query for retrieving technician revenue data.
   *
   * @param query the {@link GetTechnicianRevenueQuery}
   *     instance containing the query parameters
   * @return a list of {@link TechnicianRevenueResource}
   *     containing the revenue data for technicians.
   */
  List<TechnicianRevenueResource> handle(GetTechnicianRevenueQuery query);
}
