package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateScheduleCommand;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.UpdateScheduleResource;

/**
 * Assembler class that transforms an UpdateScheduleResource into an UpdateScheduleCommand.
 */
public class UpdateScheduleCommandFromResourceAssembler {

  /**
   * Updates a schedule with the information from the provided resource.
   *
   * @param scheduleId the ID of the schedule to update
   * @param resource the resource containing the updated schedule information
   * @return the command to update the schedule
   */
  public static UpdateScheduleCommand toCommandFromResource(
      final Long scheduleId, final UpdateScheduleResource resource) {
    return new UpdateScheduleCommand(
        scheduleId,
        resource.technicianId(), resource.day(),
        resource.startTime(), resource.endTime());
  }
}
