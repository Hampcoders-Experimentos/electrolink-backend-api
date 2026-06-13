package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.commands.CreateScheduleCommand;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateScheduleResource;

/**
 * Assembler class responsible for transforming a CreateScheduleResource
 * into a CreateScheduleCommand.
 */
public class CreateScheduleCommandFromResourceAssembler {

  /**
   * Converts a CreateScheduleResource into a CreateScheduleCommand.
   *
   * @param resource The CreateScheduleResource containing the data to be transformed.
   * @return A CreateScheduleCommand object populated with the data from the resource.
   */
  public static CreateScheduleCommand toCommandFromResource(final CreateScheduleResource resource) {
    return new CreateScheduleCommand(
        resource.technicianId(), resource.day(),
        resource.startTime(), resource.endTime());
  }
}
