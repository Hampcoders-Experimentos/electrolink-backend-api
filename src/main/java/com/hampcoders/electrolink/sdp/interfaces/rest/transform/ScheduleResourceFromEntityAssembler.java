package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ScheduleAggregate;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.ScheduleResource;

public class ScheduleResourceFromEntityAssembler {
  public static ScheduleResource toResourceFromEntity(final ScheduleAggregate entity) {
    return new ScheduleResource(
        entity.getId(), entity.getTechnicianId(),
        entity.getDay(), entity.getStartTime(), entity.getEndTime());
  }
}
