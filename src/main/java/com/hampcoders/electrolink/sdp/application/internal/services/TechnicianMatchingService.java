package com.hampcoders.electrolink.sdp.application.internal.services;

import com.hampcoders.electrolink.assets.interfaces.acl.InventoryContextFacade;
import com.hampcoders.electrolink.profiles.interfaces.rest.resources.ProfileResource;
import com.hampcoders.electrolink.sdp.application.internal.outboundservices.IExternalProfileService;
import com.hampcoders.electrolink.sdp.domain.model.aggregates.Request;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TechnicianMatchingService {

  private static final Logger log = LoggerFactory.getLogger(TechnicianMatchingService.class);

  private final IExternalProfileService externalProfileService;
  private final ScheduleRepository scheduleRepository;
  private final InventoryContextFacade inventoryContextFacade;

  public TechnicianMatchingService(IExternalProfileService externalProfileService,
                                   ScheduleRepository scheduleRepository,
                                   InventoryContextFacade inventoryContextFacade) {
    this.externalProfileService = externalProfileService;
    this.scheduleRepository = scheduleRepository;
    this.inventoryContextFacade = inventoryContextFacade;
  }

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
          log.warn("Error checking technician {} in priority fallback: {}", techIdStr, e.getMessage());
        }
      }
    }

    return Optional.empty();
  }

  private boolean matchesAll(ProfileResource technician, String techIdStr, String dayOfWeek) {
    return checkScheduleAvailability(techIdStr, dayOfWeek)
        && checkStockAvailability(technician.id());
  }

  private boolean matchesScheduleOnly(ProfileResource technician, String techIdStr, String dayOfWeek) {
    return checkScheduleAvailability(techIdStr, dayOfWeek);
  }

  private boolean checkScheduleAvailability(String technicianId, String dayOfWeek) {
    if (dayOfWeek == null) return true;
    var schedules = scheduleRepository.findByTechnicianId(technicianId);
    if (schedules.isEmpty()) return true;
    return schedules.stream().anyMatch(s -> dayOfWeek.equalsIgnoreCase(s.getDay()));
  }

  private boolean checkStockAvailability(Long technicianId) {
    return inventoryContextFacade.existsInventoryForTechnician(technicianId);
  }
}