package com.hampcoders.electrolink.sdp.application.internal.services;

import com.hampcoders.electrolink.assets.interfaces.acl.InventoryContextFacade;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import com.hampcoders.electrolink.sdp.application.internal.outboundservices.ExternalProfileService;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for matching service requests to available technicians
 * based on schedule and inventory.
 */
@Service
public class TechnicianMatchingService {

  private static final Logger log = LoggerFactory.getLogger(TechnicianMatchingService.class);

  private final ExternalProfileService externalProfileService;
  private final ScheduleRepository scheduleRepository;
  private final InventoryContextFacade inventoryContextFacade;

  /**
   * Constructor for TechnicianMatchingService.
   *
   * @param externalProfileService Service to fetch technician profiles from an external system.
   * @param scheduleRepository Repository to access technician schedules.
   * @param inventoryContextFacade Facade to check inventory availability for technicians.
   */
  public TechnicianMatchingService(ExternalProfileService externalProfileService,
                                   ScheduleRepository scheduleRepository,
                                   InventoryContextFacade inventoryContextFacade) {
    this.externalProfileService = externalProfileService;
    this.scheduleRepository = scheduleRepository;
    this.inventoryContextFacade = inventoryContextFacade;
  }

  /**
   * Finds the best available technician for a given service request based
   * on schedule and inventory.
   *
   * @param request The service request for which to find a technician.
   * @return An Optional containing the technician ID if a match is found,
   *     or empty if no suitable technician is available.
   */
  public Optional<String> findBestTechnicianForRequest(Request request) {
    List<ProfileResource> technicians = externalProfileService.fetchTechnicians();
    if (technicians.isEmpty()) {
      return Optional.empty();
    }

    String dayOfWeek = request.getScheduledDate() != null
        ? request.getScheduledDate().getDayOfWeek().name()
        : null;

    for (var technician : technicians) {
      var techIdStr = String.valueOf(technician.id());
      try {
        if (matchesAll(technician, techIdStr, dayOfWeek)) {
          return Optional.of(techIdStr);
        }
      } catch (Exception e) {
        log.warn("Error checking technician {}: {}", techIdStr, e.getMessage());
      }
    }

    if (request.isPriority()) {
      for (var technician : technicians) {
        var techIdStr = String.valueOf(technician.id());
        try {
          if (matchesScheduleOnly(technician, techIdStr, dayOfWeek)) {
            return Optional.of(techIdStr);
          }
        } catch (Exception e) {
          log.warn("Error checking technician {} in priority fallback: {}",
              techIdStr, e.getMessage());
        }
      }
    }

    return Optional.empty();
  }

  /**
   * Checks if a technician matches both schedule and inventory requirements for the request.
   *
   * @param technician The technician profile to check.
   * @param techIdStr The technician ID as a string.
   * @param dayOfWeek The day of the week for the scheduled service, or null if not specified.
   * @return true if the technician matches both schedule and inventory requirements,
   *     false otherwise.
   */
  private boolean matchesAll(ProfileResource technician, String techIdStr, String dayOfWeek) {
    return checkScheduleAvailability(techIdStr, dayOfWeek)
        && checkStockAvailability(technician.id());
  }

  /**
   * Checks if a technician matches only the schedule requirements for the request.
   *
   * @param technician The technician profile to check.
   * @param techIdStr The technician ID as a string.
   * @param dayOfWeek The day of the week for the scheduled service, or null if not specified.
   * @return true if the technician matches the schedule requirements, false otherwise.
   */
  private boolean matchesScheduleOnly(ProfileResource technician,
                                      String techIdStr, String dayOfWeek) {
    return checkScheduleAvailability(techIdStr, dayOfWeek);
  }

  /**
   * Checks if a technician is available on the specified day of the week.
   *
   * @param technicianId The ID of the technician to check.
   * @param dayOfWeek The day of the week for the scheduled service, or null if not specified.
   * @return true if the technician is available on the specified day,
   *     or if no day is specified; false otherwise.
   */
  private boolean checkScheduleAvailability(String technicianId, String dayOfWeek) {
    if (dayOfWeek == null) {
      return true;
    }
    var schedules = scheduleRepository.findByTechnicianId(technicianId);
    if (schedules.isEmpty()) {
      return true;
    }
    return schedules.stream().anyMatch(s -> dayOfWeek.equalsIgnoreCase(s.getDay()));
  }

  private boolean checkStockAvailability(Long technicianId) {
    return inventoryContextFacade.existsInventoryForTechnician(technicianId);
  }
}