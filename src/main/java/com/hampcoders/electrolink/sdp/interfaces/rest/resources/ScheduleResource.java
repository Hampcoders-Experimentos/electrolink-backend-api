package com.hampcoders.electrolink.sdp.interfaces.rest.resources;

/**
 * Resource representing a schedule for a technician.
 *
 * @param id           The unique identifier of the schedule.
 *
 * @param technicianId The ID of the technician.
 *
 * @param day          The day of the schedule (e.g., "Monday").
 *
 * @param startTime    The start time of the schedule (e.g., "09:00").
 *
 * @param endTime      The end time of the schedule (e.g., "17:00").
 */
public record ScheduleResource(
        Long id,
        String technicianId,
        String day,
        String startTime,
        String endTime
) {}
