package com.hampcoders.electrolink.analytics.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.analytics.domain.model.queries.GetHomeOwnerConsumptionQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianPerformanceQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianRevenueQuery;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.HomeOwnerConsumptionResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianPerformanceResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianRevenueResource;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.PropertyRepository;
import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Rating;
import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.RatingRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.domain.model.entities.Bill;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsQueryServiceImplTest {

  @Mock
  private ServiceOperationRepository serviceOperationRepository;
  @Mock
  private RequestRepository requestRepository;
  @Mock
  private PropertyRepository propertyRepository;
  @Mock
  private RatingRepository ratingRepository;

  @InjectMocks
  private AnalyticsQueryServiceImpl analyticsQueryService;

  // ---------- GetHomeOwnerConsumptionQuery ----------

  @Test
  @DisplayName("Given a recent request with a bill, when handling consumption, then it aggregates the period")
  void handle_ShouldAggregateConsumption_WhenRecentRequestHasBill() {
    // Arrange
    GetHomeOwnerConsumptionQuery query = new GetHomeOwnerConsumptionQuery(1L, 6);
    LocalDate today = LocalDate.now(ZoneId.systemDefault());
    Bill bill = mock(Bill.class);
    when(bill.getEnergyConsumed()).thenReturn(100.0);
    when(bill.getAmountPaid()).thenReturn(50.0);
    Request request = mock(Request.class);
    when(request.getCreatedAt()).thenReturn(Date.from(Instant.now()));
    when(request.getBill()).thenReturn(bill);
    when(requestRepository.findByClientId("1")).thenReturn(List.of(request));

    // Act
    List<HomeOwnerConsumptionResource> result = analyticsQueryService.handle(query);

    // Assert
    assertEquals(1, result.size());
    HomeOwnerConsumptionResource resource = result.getFirst();
    assertEquals(1L, resource.ownerId());
    assertEquals(today.getYear(), resource.year());
    assertEquals(today.getMonthValue(), resource.month());
    assertEquals(100.0, resource.energyConsumed());
    assertEquals(50.0, resource.amountPaid());
    assertEquals(1, resource.serviceRequestsCount());
  }

  @Test
  @DisplayName("Given no requests, when handling consumption, then it returns an empty list")
  void handle_ShouldReturnEmpty_WhenNoConsumptionRequests() {
    // Arrange
    GetHomeOwnerConsumptionQuery query = new GetHomeOwnerConsumptionQuery(1L, 6);
    when(requestRepository.findByClientId("1")).thenReturn(List.of());

    // Act
    List<HomeOwnerConsumptionResource> result = analyticsQueryService.handle(query);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a recent request without a bill, when handling consumption, then totals are zero")
  void handle_ShouldReturnZeroTotals_WhenConsumptionRequestHasNoBill() {
    // Arrange
    GetHomeOwnerConsumptionQuery query = new GetHomeOwnerConsumptionQuery(1L, 6);
    Request request = mock(Request.class);
    when(request.getCreatedAt()).thenReturn(Date.from(Instant.now()));
    when(request.getBill()).thenReturn(null);
    when(requestRepository.findByClientId("1")).thenReturn(List.of(request));

    // Act
    List<HomeOwnerConsumptionResource> result = analyticsQueryService.handle(query);

    // Assert
    assertEquals(1, result.size());
    assertEquals(0.0, result.getFirst().energyConsumed());
    assertEquals(0.0, result.getFirst().amountPaid());
    assertEquals(1, result.getFirst().serviceRequestsCount());
  }

  // ---------- GetTechnicianPerformanceQuery ----------

  @Test
  @DisplayName("Given completed and pending operations with ratings, when handling performance, then it computes metrics")
  void handle_ShouldComputeMetrics_WhenPerformanceDataExists() {
    // Arrange
    GetTechnicianPerformanceQuery query = new GetTechnicianPerformanceQuery(7L);
    OffsetDateTime start = OffsetDateTime.now();

    ServiceOperation completedOne = mock(ServiceOperation.class);
    when(completedOne.getCurrentStatus()).thenReturn(ServiceStatus.COMPLETED);
    when(completedOne.getStartedAt()).thenReturn(start);
    when(completedOne.getCompletedAt()).thenReturn(start.plusMinutes(60));

    ServiceOperation completedTwo = mock(ServiceOperation.class);
    when(completedTwo.getCurrentStatus()).thenReturn(ServiceStatus.COMPLETED);
    when(completedTwo.getStartedAt()).thenReturn(start);
    when(completedTwo.getCompletedAt()).thenReturn(start.plusMinutes(120));

    ServiceOperation pending = mock(ServiceOperation.class);
    when(pending.getCurrentStatus()).thenReturn(ServiceStatus.PENDING);

    Rating ratingFour = mock(Rating.class);
    when(ratingFour.getScore()).thenReturn(4);
    Rating ratingFive = mock(Rating.class);
    when(ratingFive.getScore()).thenReturn(5);

    when(serviceOperationRepository.findByTechnicianId(any(TechnicianId.class)))
        .thenReturn(List.of(completedOne, completedTwo, pending));
    when(ratingRepository.findByTechnicianId(any(TechnicianId.class)))
        .thenReturn(List.of(ratingFour, ratingFive));

    // Act
    List<TechnicianPerformanceResource> result = analyticsQueryService.handle(query);

    // Assert
    assertEquals(1, result.size());
    TechnicianPerformanceResource resource = result.getFirst();
    assertEquals(7L, resource.technicianId());
    assertEquals(2, resource.totalServicesCompleted());
    assertEquals(1, resource.pendingServices());
    assertEquals(4.5, resource.averageRating());
    assertEquals(1.5, resource.averageCompletionTimeHours());
  }

  @Test
  @DisplayName("Given no operations or ratings, when handling performance, then metrics are zero")
  void handle_ShouldReturnZeroMetrics_WhenNoPerformanceData() {
    // Arrange
    GetTechnicianPerformanceQuery query = new GetTechnicianPerformanceQuery(7L);
    when(serviceOperationRepository.findByTechnicianId(any(TechnicianId.class)))
        .thenReturn(List.of());
    when(ratingRepository.findByTechnicianId(any(TechnicianId.class)))
        .thenReturn(List.of());

    // Act
    List<TechnicianPerformanceResource> result = analyticsQueryService.handle(query);

    // Assert
    assertEquals(1, result.size());
    TechnicianPerformanceResource resource = result.getFirst();
    assertEquals(0, resource.totalServicesCompleted());
    assertEquals(0, resource.pendingServices());
    assertEquals(0.0, resource.averageRating());
    assertEquals(0.0, resource.averageCompletionTimeHours());
  }

  @Test
  @DisplayName("Given a completed operation lacking timestamps and a null score, when handling performance, then time and rating are zero")
  void handle_ShouldIgnoreIncompleteData_WhenPerformanceTimestampsMissing() {
    // Arrange
    GetTechnicianPerformanceQuery query = new GetTechnicianPerformanceQuery(7L);

    ServiceOperation completed = mock(ServiceOperation.class);
    when(completed.getCurrentStatus()).thenReturn(ServiceStatus.COMPLETED);
    when(completed.getCompletedAt()).thenReturn(null);

    Rating nullScore = mock(Rating.class);
    when(nullScore.getScore()).thenReturn(null);

    when(serviceOperationRepository.findByTechnicianId(any(TechnicianId.class)))
        .thenReturn(List.of(completed));
    when(ratingRepository.findByTechnicianId(any(TechnicianId.class)))
        .thenReturn(List.of(nullScore));

    // Act
    List<TechnicianPerformanceResource> result = analyticsQueryService.handle(query);

    // Assert
    assertEquals(1, result.size());
    TechnicianPerformanceResource resource = result.getFirst();
    assertEquals(1, resource.totalServicesCompleted());
    assertEquals(0.0, resource.averageCompletionTimeHours());
    assertEquals(0.0, resource.averageRating());
  }

  // ---------- GetTechnicianRevenueQuery ----------

  @Test
  @DisplayName("Given a completed operation with a paid request, when handling revenue, then it sums the period revenue")
  void handle_ShouldSumRevenue_WhenCompletedOperationHasPaidRequest() {
    // Arrange
    GetTechnicianRevenueQuery query = new GetTechnicianRevenueQuery(7L, 6);

    ServiceOperation operation = mock(ServiceOperation.class);
    when(operation.getCurrentStatus()).thenReturn(ServiceStatus.COMPLETED);
    when(operation.getCompletedAt()).thenReturn(OffsetDateTime.now());
    when(operation.getRequestId()).thenReturn(new RequestId(10L));

    Bill bill = mock(Bill.class);
    when(bill.getAmountPaid()).thenReturn(200.0);
    Request request = mock(Request.class);
    when(request.getBill()).thenReturn(bill);

    when(serviceOperationRepository.findByTechnicianId(any(TechnicianId.class)))
        .thenReturn(List.of(operation));
    when(requestRepository.findById(10L)).thenReturn(Optional.of(request));

    // Act
    List<TechnicianRevenueResource> result = analyticsQueryService.handle(query);

    // Assert
    assertEquals(1, result.size());
    TechnicianRevenueResource resource = result.getFirst();
    assertEquals(7L, resource.technicianId());
    assertEquals(YearMonth.now().toString(), resource.period());
    assertEquals(200.0, resource.totalRevenue());
    assertEquals(1, resource.servicesCount());
    assertEquals(200.0, resource.averageRevenuePerService());
  }

  @Test
  @DisplayName("Given no completed operations, when handling revenue, then it returns an empty list")
  void handle_ShouldReturnEmpty_WhenNoRevenueOperations() {
    // Arrange
    GetTechnicianRevenueQuery query = new GetTechnicianRevenueQuery(7L, 6);
    when(serviceOperationRepository.findByTechnicianId(any(TechnicianId.class)))
        .thenReturn(List.of());

    // Act
    List<TechnicianRevenueResource> result = analyticsQueryService.handle(query);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a completed operation whose request is missing, when handling revenue, then revenue is zero")
  void handle_ShouldReturnZeroRevenue_WhenRequestNotFound() {
    // Arrange
    GetTechnicianRevenueQuery query = new GetTechnicianRevenueQuery(7L, 6);

    ServiceOperation operation = mock(ServiceOperation.class);
    when(operation.getCurrentStatus()).thenReturn(ServiceStatus.COMPLETED);
    when(operation.getCompletedAt()).thenReturn(OffsetDateTime.now());
    when(operation.getRequestId()).thenReturn(new RequestId(10L));

    when(serviceOperationRepository.findByTechnicianId(any(TechnicianId.class)))
        .thenReturn(List.of(operation));
    when(requestRepository.findById(10L)).thenReturn(Optional.empty());

    // Act
    List<TechnicianRevenueResource> result = analyticsQueryService.handle(query);

    // Assert
    assertEquals(1, result.size());
    TechnicianRevenueResource resource = result.getFirst();
    assertEquals(0.0, resource.totalRevenue());
    assertEquals(1, resource.servicesCount());
    assertEquals(0.0, resource.averageRevenuePerService());
  }
}
