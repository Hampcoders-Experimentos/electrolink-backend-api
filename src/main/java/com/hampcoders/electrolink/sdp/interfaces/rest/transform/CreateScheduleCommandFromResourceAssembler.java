package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.commands.CreateScheduleCommand;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.CreateScheduleResource;

public class CreateScheduleCommandFromResourceAssembler {
  public static CreateScheduleCommand toCommandFromResource(final CreateScheduleResource resource) {
    return new CreateScheduleCommand(
        resource.technicianId(), resource.day(),
        resource.startTime(), resource.endTime());
  }
}
