package com.hampcoders.electrolink.analytics.application.internal.queryservices;

import com.hampcoders.electrolink.analytics.domain.model.queries.GetHomeOwnerConsumptionQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianPerformanceQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianRevenueQuery;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalyticsQueryServiceImplTest {

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

    @Test
    @DisplayName("handle(GetHomeOwnerConsumptionQuery) should return aggregated consumption data (AAA)")
    void handle_GetHomeOwnerConsumption_ShouldReturnAggregatedData() {
        // Arrange
        Long ownerId = 1L;
        var query = new GetHomeOwnerConsumptionQuery(ownerId, 12);

        var request1 = mock(Request.class);
        var bill1 = new Bill("2023-10", 100.0, 50.0, "url");
        when(request1.getBill()).thenReturn(bill1);
        when(request1.getCreatedAt()).thenReturn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        var request2 = mock(Request.class);
        when(request2.getBill()).thenReturn(null);
        when(request2.getCreatedAt()).thenReturn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        when(requestRepository.findByClientId(String.valueOf(ownerId))).thenReturn(List.of(request1, request2));

        // Act
        var result = analyticsQueryService.handle(query);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        var res = result.getFirst();
        assertEquals(ownerId, res.ownerId());
        assertEquals(100.0, res.energyConsumed());
        assertEquals(50.0, res.amountPaid());
        assertEquals(2, res.serviceRequestsCount());
    }

    @Test
    @DisplayName("handle(GetTechnicianPerformanceQuery) should return performance metrics (AAA)")
    void handle_GetTechnicianPerformance_ShouldReturnMetrics() {
        // Arrange
        Long techId = 1L;
        var query = new GetTechnicianPerformanceQuery(techId);

        var op1 = mock(ServiceOperation.class);
        when(op1.getCurrentStatus()).thenReturn(ServiceStatus.COMPLETED);
        when(op1.getStartedAt()).thenReturn(OffsetDateTime.now().minusHours(2));
        when(op1.getCompletedAt()).thenReturn(OffsetDateTime.now());

        var op2 = mock(ServiceOperation.class);
        when(op2.getCurrentStatus()).thenReturn(ServiceStatus.PENDING);

        when(serviceOperationRepository.findByTechnicianId(any(TechnicianId.class))).thenReturn(List.of(op1, op2));

        var rating1 = mock(Rating.class);
        when(rating1.getScore()).thenReturn(5);
        var rating2 = mock(Rating.class);
        when(rating2.getScore()).thenReturn(4);

        when(ratingRepository.findByTechnicianId(any(TechnicianId.class))).thenReturn(List.of(rating1, rating2));

        // Act
        var result = analyticsQueryService.handle(query);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        var res = result.getFirst();
        assertEquals(techId, res.technicianId());
        assertEquals(1, res.totalServicesCompleted());
        assertEquals(1, res.pendingServices());
        assertEquals(4.5, res.averageRating());
        assertEquals(2.0, res.averageCompletionTimeHours());
    }

    @Test
    @DisplayName("handle(GetTechnicianRevenueQuery) should return aggregated revenue data (AAA)")
    void handle_GetTechnicianRevenue_ShouldReturnAggregatedData() {
        // Arrange
        Long techId = 1L;
        var query = new GetTechnicianRevenueQuery(techId, 12);

        var op1 = mock(ServiceOperation.class);
        when(op1.getCurrentStatus()).thenReturn(ServiceStatus.COMPLETED);
        when(op1.getCompletedAt()).thenReturn(OffsetDateTime.now());
        when(op1.getRequestId()).thenReturn(new RequestId(10L));

        when(serviceOperationRepository.findByTechnicianId(any(TechnicianId.class))).thenReturn(List.of(op1));

        var request = mock(Request.class);
        var bill = new Bill("2023-10", 0.0, 150.0, "url");
        when(request.getBill()).thenReturn(bill);
        when(requestRepository.findById(10L)).thenReturn(Optional.of(request));

        // Act
        var result = analyticsQueryService.handle(query);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        var res = result.getFirst();
        assertEquals(techId, res.technicianId());
        assertEquals(150.0, res.totalRevenue());
        assertEquals(1, res.servicesCount());
        assertEquals(150.0, res.averageRevenuePerService());
    }
}
