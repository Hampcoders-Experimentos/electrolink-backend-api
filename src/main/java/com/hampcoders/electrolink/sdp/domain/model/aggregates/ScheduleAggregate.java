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

  protected ScheduleAggregate() {
  }

  public ScheduleAggregate(final CreateScheduleCommand command) {
    this.technicianId = command.technicianId();
    this.day = command.day();
    this.startTime = command.startTime();
    this.endTime = command.endTime();
  }

  public void updateFrom(final UpdateScheduleCommand command) {
    this.technicianId = command.technicianId();
    this.day = command.day();
    this.startTime = command.startTime();
    this.endTime = command.endTime();
  }
}
