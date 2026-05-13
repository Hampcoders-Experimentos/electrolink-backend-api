package com.hampcoders.electrolink.sdp.application.internal.queryservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindScheduleByIdQuery;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindSchedulesByTechnicianIdQuery;
import com.hampcoders.electrolink.sdp.domain.services.ScheduleQueryService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class ScheduleQueryServiceImpl implements ScheduleQueryService {

  private final ScheduleRepository scheduleRepository;

  public ScheduleQueryServiceImpl(ScheduleRepository scheduleRepository) {
    this.scheduleRepository = scheduleRepository;
  }

  @Override
  public Optional<ScheduleAggregate> handle(FindScheduleByIdQuery query) {
    return scheduleRepository.findById(query.scheduleId());
  }

  @Override
  public List<ScheduleAggregate> handle(FindSchedulesByTechnicianIdQuery query) {
    return scheduleRepository.findByTechnicianId(query.technicianId());
  }
}
