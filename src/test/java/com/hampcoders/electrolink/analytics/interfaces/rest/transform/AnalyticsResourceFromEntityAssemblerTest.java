package com.hampcoders.electrolink.analytics.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hampcoders.electrolink.analytics.interfaces.rest.resources.HomeOwnerConsumptionResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianPerformanceResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianRevenueResource;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnalyticsResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a populated list, when assembling consumption list, then it returns the same list")
  void handle_ShouldReturnSameList_WhenConsumptionListProvided() {
    // Arrange
    List<HomeOwnerConsumptionResource> resources =
        List.of(new HomeOwnerConsumptionResource(1L, 6, 2026, 100.0, 50.0, 1));

    // Act
    List<HomeOwnerConsumptionResource> result =
        AnalyticsResourceFromEntityAssembler.toHomeOwnerConsumptionResourceList(resources);

    // Assert
    assertSame(resources, result);
  }

  @Test
  @DisplayName("Given a null list, when assembling consumption list, then it returns null")
  void handle_ShouldReturnNull_WhenConsumptionListIsNull() {
    // Act
    List<HomeOwnerConsumptionResource> result =
        AnalyticsResourceFromEntityAssembler.toHomeOwnerConsumptionResourceList(null);

    // Assert
    assertNull(result);
  }

  @Test
  @DisplayName("Given an empty list, when assembling consumption list, then it returns an empty list")
  void handle_ShouldReturnEmptyList_WhenConsumptionListIsEmpty() {
    // Act
    List<HomeOwnerConsumptionResource> result =
        AnalyticsResourceFromEntityAssembler.toHomeOwnerConsumptionResourceList(List.of());

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given performance values, when assembling, then it maps them into the resource")
  void handle_ShouldBuildPerformanceResource_WhenValuesProvided() {
    // Act
    TechnicianPerformanceResource result =
        AnalyticsResourceFromEntityAssembler.toTechnicianPerformanceResource(
            7L, 5, 4.5, 1.5, 2);

    // Assert
    assertEquals(7L, result.technicianId());
    assertEquals(5, result.totalServicesCompleted());
    assertEquals(4.5, result.averageRating());
    assertEquals(1.5, result.averageCompletionTimeHours());
    assertEquals(2, result.pendingServices());
  }

  @Test
  @DisplayName("Given revenue values, when assembling, then it maps them into the resource")
  void handle_ShouldBuildRevenueResource_WhenValuesProvided() {
    // Act
    TechnicianRevenueResource result =
        AnalyticsResourceFromEntityAssembler.toTechnicianRevenueResource(
            7L, "2026-06", 200.0, 1, 200.0);

    // Assert
    assertEquals(7L, result.technicianId());
    assertEquals("2026-06", result.period());
    assertEquals(200.0, result.totalRevenue());
    assertEquals(1, result.servicesCount());
    assertEquals(200.0, result.averageRevenuePerService());
  }
}
