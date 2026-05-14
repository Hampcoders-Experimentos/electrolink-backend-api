package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.ScheduleResource;

/**
 * Assembler class to convert ScheduleAggregate entities to ScheduleResources.
 */
public class ScheduleResourceFromEntityAssembler {

  /**
   * Converts a ScheduleAggregate entity to a ScheduleResource.
   *
   * @param entity the ScheduleAggregate entity to convert
   * @return a ScheduleResource representing the given ScheduleAggregate entity
   */
  public static ScheduleResource toResourceFromEntity(final ScheduleAggregate entity) {
    return new ScheduleResource(
        entity.getId(), entity.getTechnicianId(),
        entity.getDay(), entity.getStartTime(), entity.getEndTime());
  }
}
