package com.hampcoders.electrolink.analytics.interfaces.rest;

import com.hampcoders.electrolink.analytics.domain.model.queries.GetHomeOwnerConsumptionQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianPerformanceQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianRevenueQuery;
import com.hampcoders.electrolink.analytics.domain.services.AnalyticsQueryService;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.HomeOwnerConsumptionResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianPerformanceResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianRevenueResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsQueryService analyticsQueryService;

    public AnalyticsController(AnalyticsQueryService analyticsQueryService) {
        this.analyticsQueryService = analyticsQueryService;
    }

    @Operation(summary = "Get energy consumption analytics for a homeowner")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consumption data retrieved successfully")
    })
    @GetMapping("/homeowners/{ownerId}/consumption")
    public ResponseEntity<List<HomeOwnerConsumptionResource>> getHomeOwnerConsumption(
        @Parameter(description = "Id of the homeowner") @PathVariable Long ownerId,
        @Parameter(description = "Number of months to include") @RequestParam(defaultValue = "12") int months) {
        var query = new GetHomeOwnerConsumptionQuery(ownerId, months);
        var result = analyticsQueryService.handle(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get performance metrics for a technician")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Performance data retrieved successfully")
    })
    @GetMapping("/technicians/{technicianId}/performance")
    public ResponseEntity<List<TechnicianPerformanceResource>> getTechnicianPerformance(
        @Parameter(description = "Id of the technician") @PathVariable Long technicianId) {
        var query = new GetTechnicianPerformanceQuery(technicianId);
        var result = analyticsQueryService.handle(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get revenue analytics for a technician")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Revenue data retrieved successfully")
    })
    @GetMapping("/technicians/{technicianId}/revenue")
    public ResponseEntity<List<TechnicianRevenueResource>> getTechnicianRevenue(
        @Parameter(description = "Id of the technician") @PathVariable Long technicianId,
        @Parameter(description = "Number of months to include") @RequestParam(defaultValue = "6") int months) {
        var query = new GetTechnicianRevenueQuery(technicianId, months);
        var result = analyticsQueryService.handle(query);
        return ResponseEntity.ok(result);
    }
}
