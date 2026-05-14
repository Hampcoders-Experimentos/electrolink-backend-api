package com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing ScheduleAggregate entities.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleAggregate, Long> {

  /**
   * Finds a list of ScheduleAggregate entities by the technician's ID.
   *
   * @param technicianId the ID of the technician whose schedules are to be retrieved
   * @return a list of ScheduleAggregate entities associated with the specified technician ID
   */
  List<ScheduleAggregate> findByTechnicianId(String technicianId);
}
