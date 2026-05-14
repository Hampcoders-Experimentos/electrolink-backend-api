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

/**
 * REST controller for managing schedules.
 */
@RestController
@RequestMapping("/api/v1")
public class ScheduleController {

  private final ScheduleCommandService commandService;
  private final ScheduleQueryService queryService;

  /**
   * Constructor for ScheduleController.
   *
   * @param commandService the service for handling schedule commands
   * @param queryService the service for handling schedule queries
   */
  public ScheduleController(ScheduleCommandService commandService,
                            ScheduleQueryService queryService) {
    this.commandService = commandService;
    this.queryService = queryService;
  }

  /**
   * Get schedules by technician ID.
   *
   * @param technicianId the ID of the technician
   * @return a list of schedule resources for the specified technician
   */
  @GetMapping("/technicians/{technicianId}/schedules")
  public ResponseEntity<List<ScheduleResource>> getByTechnician(@PathVariable String technicianId) {
    var query = new FindSchedulesByTechnicianIdQuery(technicianId);
    var result = queryService.handle(query);
    var resources = result.stream()
        .map(ScheduleResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return ResponseEntity.ok(resources);
  }

  /**
   * Create a new schedule.
   *
   * @param resource the resource containing the schedule details
   * @return the ID of the created schedule containing the details of the schedule to be created
   */
  @PostMapping("/schedules")
  public ResponseEntity<Long> create(@RequestBody CreateScheduleResource resource) {
    var command = CreateScheduleCommandFromResourceAssembler.toCommandFromResource(resource);
    var createdId = commandService.handle(command);
    return ResponseEntity.ok(createdId);
  }

  /**
   * Update an existing schedule.
   *
   * @param scheduleId the ID of the schedule to update
   * @param resource the resource containing the updated schedule details
   * @return a response entity indicating the result of the update operation
   */
  @PutMapping("/schedules/{scheduleId}")
  public ResponseEntity<Void> update(@PathVariable Long scheduleId,
                                     @RequestBody UpdateScheduleResource resource) {
    var command = UpdateScheduleCommandFromResourceAssembler
        .toCommandFromResource(scheduleId, resource);
    commandService.handle(command);
    return ResponseEntity.ok().build();
  }

  /**
   * Delete a schedule by ID.
   *
   * @param scheduleId the ID of the schedule to delete
   * @return a response entity indicating the result of the delete operation
   *     of the schedule to delete
   */
  @DeleteMapping("/schedules/{scheduleId}")
  public ResponseEntity<Void> delete(@PathVariable Long scheduleId) {
    var command = new DeleteScheduleCommand(scheduleId);
    commandService.handle(command);
    return ResponseEntity.ok().build();
  }
}
