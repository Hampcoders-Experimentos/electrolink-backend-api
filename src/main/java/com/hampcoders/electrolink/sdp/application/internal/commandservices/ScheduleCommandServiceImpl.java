package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateScheduleCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteScheduleCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateScheduleCommand;
import com.hampcoders.electrolink.sdp.domain.services.ScheduleCommandService;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the ScheduleCommandService interface.
 */
@Service
public class ScheduleCommandServiceImpl implements ScheduleCommandService {

  private final ScheduleRepository scheduleRepository;

  /**
   * Constructor for ScheduleCommandServiceImpl.
   *
   * @param scheduleRepository the repository for managing ScheduleAggregate entities
   */
  public ScheduleCommandServiceImpl(ScheduleRepository scheduleRepository) {
    this.scheduleRepository = scheduleRepository;
  }

  /**
   * Handles the creation of a new schedule based on the provided command.
   *
   * @param command The command containing the data for the new schedule.
   * @return The ID of the newly created schedule.
   */
  @Override
  @Transactional
  public Long handle(CreateScheduleCommand command) {
    var schedule = new ScheduleAggregate(command);
    return scheduleRepository.save(schedule).getId();
  }

  /**
   * Handles the update of an existing schedule based on the provided command.
   *
   * @param command The command containing the updated data for the schedule.
   */
  @Override
  @Transactional
  public void handle(UpdateScheduleCommand command) {
    var schedule = scheduleRepository.findById(command.scheduleId())
        .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    schedule.updateFrom(command);
    scheduleRepository.save(schedule);
  }

  /**
   * Handles the deletion of a schedule based on the provided command.
   *
   * @param command The command containing the ID of the schedule to delete.
   */
  @Override
  @Transactional
  public void handle(DeleteScheduleCommand command) {
    if (!scheduleRepository.existsById(command.scheduleId())) {
      throw new IllegalArgumentException("Schedule not found");
    }
    scheduleRepository.deleteById(command.scheduleId());
  }
}
