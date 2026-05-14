package com.hampcoders.electrolink.monitoring.application.internal.queryservices;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.Report;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetAllReportsQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetReportByIdQuery;
import com.hampcoders.electrolink.monitoring.domain.model.queries.GetReportsByServiceOperationIdQuery;
import com.hampcoders.electrolink.monitoring.domain.services.ReportQueryService;
import com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories.ReportRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the query service for Report entities.
 */
@Service
public class ReportQueryServiceImpl implements ReportQueryService {

  private final ReportRepository reportRepository;

  /**
   * Constructor for ReportQueryServiceImpl, injecting the ReportRepository.
   *
   * @param reportRepository The repository used to access Report entities from the database.
   */
  public ReportQueryServiceImpl(ReportRepository reportRepository) {
    this.reportRepository = reportRepository;
  }

  /**
   * Handles the query to retrieve all reports.
   *
   * @param query The query object (placeholder).
   * @return A list of all Report entities.
   */
  @Override
  public List<Report> handle(GetAllReportsQuery query) {
    return reportRepository.findAll();
  }

  /**
   * Handles the query to retrieve a report by its ID.
   *
   * @param query The query object containing the report ID.
   * @return An Optional containing the Report entity, or empty if not found.
   */
  @Override
  public Optional<Report> handle(GetReportByIdQuery query) {
    return reportRepository.findById(query.reportId());
  }

  /**
   * Handles the query to retrieve all reports associated with a specific request ID.
   *
   * @param query The query object containing the request ID.
   * @return A list of Report entities matching the request ID.
   */
  @Override
  public List<Report> handle(GetReportsByServiceOperationIdQuery query) {
    return reportRepository.findByServiceOperationId(query.serviceOperationId());
  }
}