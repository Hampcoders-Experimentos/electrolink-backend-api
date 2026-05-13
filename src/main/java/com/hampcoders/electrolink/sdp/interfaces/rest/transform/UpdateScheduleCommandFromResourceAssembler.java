package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateScheduleCommand;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.UpdateScheduleResource;

public class UpdateScheduleCommandFromResourceAssembler {
  public static UpdateScheduleCommand toCommandFromResource(
      final Long scheduleId, final UpdateScheduleResource resource) {
    return new UpdateScheduleCommand(
        scheduleId,
        resource.technicianId(), resource.day(),
        resource.startTime(), resource.endTime());
  }
}
