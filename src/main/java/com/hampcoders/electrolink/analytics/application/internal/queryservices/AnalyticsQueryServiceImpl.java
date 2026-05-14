package com.hampcoders.electrolink.analytics.application.internal.queryservices;

import com.hampcoders.electrolink.analytics.domain.model.queries.GetHomeOwnerConsumptionQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianPerformanceQuery;
import com.hampcoders.electrolink.analytics.domain.model.queries.GetTechnicianRevenueQuery;
import com.hampcoders.electrolink.analytics.domain.services.AnalyticsQueryService;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.HomeOwnerConsumptionResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianPerformanceResource;
import com.hampcoders.electrolink.analytics.interfaces.rest.resources.TechnicianRevenueResource;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.PropertyRepository;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.RatingRepository;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ServiceOperationRepository;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.RequestRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Implementation of AnalyticsQueryService that handles queries related to homeowner consumption,
 * technician performance, and technician revenue.
 * It interacts with repositories to fetch necessary data
 * and processes it to produce the required analytics results.
 */
@Service
public class AnalyticsQueryServiceImpl implements AnalyticsQueryService {

  private final ServiceOperationRepository serviceOperationRepository;
  private final RequestRepository requestRepository;
  private final RatingRepository ratingRepository;

  /**
   * Analytics Query Service Implementation constructor
   * that initializes the required repositories for data access.
   *
   * @param serviceOperationRepository Repository of service
   *     operations to fetch technician performance and revenue data.
   * @param requestRepository Repository of service requests
   *     to fetch homeowner consumption and technician revenue data.
   * @param propertyRepository Repository of properties.
   * @param ratingRepository Repository of ratings to fetch technician performance data.
   */
  public AnalyticsQueryServiceImpl(ServiceOperationRepository serviceOperationRepository,
                                   RequestRepository requestRepository,
                                   PropertyRepository propertyRepository,
                                   RatingRepository ratingRepository) {
    this.serviceOperationRepository = serviceOperationRepository;
    this.requestRepository = requestRepository;
    this.ratingRepository = ratingRepository;
  }

  @Override
  public List<HomeOwnerConsumptionResource> handle(GetHomeOwnerConsumptionQuery query) {
    var requests = requestRepository.findByClientId(String.valueOf(query.ownerId()));

    var cutoff = LocalDate.now().minusMonths(query.months());

    return requests.stream()
        .filter(r -> {
          var requestDate = r.getCreatedAt().toInstant()
              .atZone(ZoneId.systemDefault()).toLocalDate();
          return !requestDate.isBefore(cutoff);
        })
        .collect(Collectors.groupingBy(r -> {
          var requestDate = r.getCreatedAt().toInstant()
              .atZone(ZoneId.systemDefault()).toLocalDate();
          return new AbstractMap.SimpleEntry<>(requestDate.getYear(), requestDate.getMonthValue());
        }))
        .entrySet().stream()
        .map(entry -> {
          var year = entry.getKey().getKey();
          var month = entry.getKey().getValue();
          var periodRequests = entry.getValue();
          var totalEnergy = periodRequests.stream()
              .mapToDouble(r -> r.getBill() != null ? r.getBill().getEnergyConsumed() : 0.0)
              .sum();
          var totalPaid = periodRequests.stream()
              .mapToDouble(r -> r.getBill() != null ? r.getBill().getAmountPaid() : 0.0)
              .sum();
          return new HomeOwnerConsumptionResource(
              query.ownerId(), month, year,
              Math.round(totalEnergy * 100.0) / 100.0,
              Math.round(totalPaid * 100.0) / 100.0,
              periodRequests.size()
          );
        })
        .sorted(Comparator.comparing(HomeOwnerConsumptionResource::year)
            .thenComparing(HomeOwnerConsumptionResource::month))
        .toList();
  }

  @Override
  public List<TechnicianPerformanceResource> handle(GetTechnicianPerformanceQuery query) {
    var techId = new TechnicianId(query.technicianId());
    var operations = serviceOperationRepository.findByTechnicianId(techId);

    var completed = operations.stream()
        .filter(op -> op.getCurrentStatus() == ServiceStatus.COMPLETED)
        .toList();

    var pending = operations.stream()
        .filter(op -> op.getCurrentStatus() == ServiceStatus.PENDING)
        .count();

    var avgCompletionTimeMinutes = completed.stream()
        .filter(op -> op.getCompletedAt() != null && op.getStartedAt() != null)
        .mapToLong(op -> Duration.between(op.getStartedAt(), op.getCompletedAt()).toMinutes())
        .average()
        .orElse(0.0);

    var ratings = ratingRepository.findByTechnicianId(techId);
    var avgRating = ratings.stream()
        .mapToInt(r -> r.getScore() != null ? r.getScore() : 0)
        .average()
        .orElse(0.0);

    var avgCompletionTimeHours = Math.round(avgCompletionTimeMinutes / 60.0 * 100.0) / 100.0;

    return List.of(new TechnicianPerformanceResource(
        query.technicianId(),
        completed.size(),
        Math.round(avgRating * 100.0) / 100.0,
        avgCompletionTimeHours,
        (int) pending
    ));
  }

  @Override
  public List<TechnicianRevenueResource> handle(GetTechnicianRevenueQuery query) {
    var techId = new TechnicianId(query.technicianId());
    var operations = serviceOperationRepository.findByTechnicianId(techId).stream()
        .filter(op -> op.getCurrentStatus() == ServiceStatus.COMPLETED
            && op.getCompletedAt() != null)
        .toList();

    var cutoff = LocalDate.now().minusMonths(query.months());

    return operations.stream()
        .collect(Collectors.groupingBy(op -> {
          var completedDate = op.getCompletedAt().toLocalDate();
          return new AbstractMap.SimpleEntry<>(completedDate.getYear(),
              completedDate.getMonthValue());
        }))
        .entrySet().stream()
        .map(entry -> {
          var year = entry.getKey().getKey();
          var month = entry.getKey().getValue();
          var periodStart = LocalDate.of(year, month, 1);
          if (periodStart.isBefore(cutoff)) {
            return null;
          }

          var opsInPeriod = entry.getValue();
          var totalRevenue = opsInPeriod.stream()
              .mapToDouble(op -> {
                var request = requestRepository.findById(op.getRequestId().requestId())
                    .orElse(null);
                return request != null && request.getBill() != null
                    ? request.getBill().getAmountPaid() : 0.0;
              })
              .sum();
          var count = opsInPeriod.size();
          var avgPerService = count > 0 ? totalRevenue / count : 0.0;
          return new TechnicianRevenueResource(
              query.technicianId(),
              year + "-" + String.format("%02d", month),
              Math.round(totalRevenue * 100.0) / 100.0,
              count,
              Math.round(avgPerService * 100.0) / 100.0
          );
        })
        .filter(Objects::nonNull)
        .sorted(Comparator.comparing(TechnicianRevenueResource::period))
        .toList();
  }
}
