package com.hampcoders.electrolink.sdp.domain.model.aggregates;

import com.hampcoders.electrolink.sdp.domain.model.commands.CreateScheduleCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateScheduleCommand;
import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Entity;
import lombok.Getter;

/**
 * Represents a technician's schedule as an aggregate root.
 */
@Entity
@Getter
public class ScheduleAggregate extends AuditableAbstractAggregateRoot<ScheduleAggregate> {

  private String technicianId;

  private String day;
  private String startTime;
  private String endTime;

  /**
   * Protected no-args constructor required by JPA.
   */
  protected ScheduleAggregate() {
  }

  /**
   * Constructs a new ScheduleAggregate based on the provided CreateScheduleCommand.
   *
   * @param command the command containing the details for creating a new schedule
   */
  public ScheduleAggregate(final CreateScheduleCommand command) {
    this.technicianId = command.technicianId();
    this.day = command.day();
    this.startTime = command.startTime();
    this.endTime = command.endTime();
  }

  /**
   * Updates the schedule aggregate with new values from the provided UpdateScheduleCommand.
   *
   * @param command the command containing the updated details for the schedule
   */
  public void updateFrom(final UpdateScheduleCommand command) {
    this.technicianId = command.technicianId();
    this.day = command.day();
    this.startTime = command.startTime();
    this.endTime = command.endTime();
  }
}
