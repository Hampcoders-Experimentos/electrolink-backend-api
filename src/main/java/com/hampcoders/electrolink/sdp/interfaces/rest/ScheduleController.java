package com.hampcoders.electrolink.sdp.interfaces.rest;

import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteScheduleCommand;
import com.hampcoders.electrolink.sdp.domain.model.queries.FindSchedulesByTechnicianIdQuery;
import com.hampcoders.electrolink.sdp.domain.services.ScheduleCommandService;
import com.hampcoders.electrolink.sdp.domain.services.ScheduleQueryService;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.CreateScheduleResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.ScheduleResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.UpdateScheduleResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.CreateScheduleCommandFromResourceAssembler;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.ScheduleResourceFromEntityAssembler;
import com.hampcoders.electrolink.sdp.interfaces.rest.transform.UpdateScheduleCommandFromResourceAssembler;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ScheduleController {

  private final ScheduleCommandService commandService;
  private final ScheduleQueryService queryService;

  public ScheduleController(ScheduleCommandService commandService,
                            ScheduleQueryService queryService) {
    this.commandService = commandService;
    this.queryService = queryService;
  }

  @GetMapping("/technicians/{technicianId}/schedules")
  public ResponseEntity<List<ScheduleResource>> getByTechnician(@PathVariable String technicianId) {
    var query = new FindSchedulesByTechnicianIdQuery(technicianId);
    var result = queryService.handle(query);
    var resources = result.stream()
        .map(ScheduleResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return ResponseEntity.ok(resources);
  }

  @PostMapping("/schedules")
  public ResponseEntity<Long> create(@RequestBody CreateScheduleResource resource) {
    var command = CreateScheduleCommandFromResourceAssembler.toCommandFromResource(resource);
    var createdId = commandService.handle(command);
    return ResponseEntity.ok(createdId);
  }

  @PutMapping("/schedules/{scheduleId}")
  public ResponseEntity<Void> update(@PathVariable Long scheduleId,
                                     @RequestBody UpdateScheduleResource resource) {
    var command = UpdateScheduleCommandFromResourceAssembler.toCommandFromResource(scheduleId, resource);
    commandService.handle(command);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/schedules/{scheduleId}")
  public ResponseEntity<Void> delete(@PathVariable Long scheduleId) {
    var command = new DeleteScheduleCommand(scheduleId);
    commandService.handle(command);
    return ResponseEntity.ok().build();
  }
}
