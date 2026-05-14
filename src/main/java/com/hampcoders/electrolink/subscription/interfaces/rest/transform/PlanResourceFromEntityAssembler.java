package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.PlanResource;

/**
 * Assembles a PlanResource from a Plan entity.
 */
public class PlanResourceFromEntityAssembler {

  /**
   * Converts a Plan entity to a PlanResource.
   *
   * @param plan the Plan entity to convert
   * @return a PlanResource representing the Plan entity
   */
  public static PlanResource toResourceFromEntity(Plan plan) {
    return new PlanResource(
        plan.getId(),
        plan.getName().name(),
        plan.getDescription(),
        plan.getPrice().amount().doubleValue(),
        plan.getMaxRequestsPerMonth(),
        plan.isPrioritySupport(),
        plan.isActive()
    );
  }
}
