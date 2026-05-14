package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindScheduleByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindSchedulesByTechnicianIdQuery;
import com.hampcoders.electrolink.sdp.domain.services.ScheduleQueryService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the ScheduleQueryService interface
 * that provides methods to handle queries related to schedules.
 */
@Service
@Transactional(readOnly = true)
public class ScheduleQueryServiceImpl implements ScheduleQueryService {

  private final ScheduleRepository scheduleRepository;

  /**
   * Constructor for ScheduleQueryServiceImpl.
   *
   * @param scheduleRepository the repository used to access schedule data
   */
  public ScheduleQueryServiceImpl(ScheduleRepository scheduleRepository) {
    this.scheduleRepository = scheduleRepository;
  }

  /**
   * Handles the FindScheduleByIdQuery by retrieving the schedule
   * with the specified ID from the repository.
   *
   * @param query The query containing the schedule ID.
   * @return An Optional containing the ScheduleAggregate if found, or empty if not found.
   */
  @Override
  public Optional<ScheduleAggregate> handle(FindScheduleByIdQuery query) {
    return scheduleRepository.findById(query.scheduleId());
  }

  /**
   * Handles the FindSchedulesByTechnicianIdQuery by retrieving all schedules
   * associated with the specified technician ID from the repository.
   *
   * @param query The query containing the technician ID.
   * @return A list of ScheduleAggregate objects associated with the technician ID.
   */
  @Override
  public List<ScheduleAggregate> handle(FindSchedulesByTechnicianIdQuery query) {
    return scheduleRepository.findByTechnicianId(query.technicianId());
  }
}
