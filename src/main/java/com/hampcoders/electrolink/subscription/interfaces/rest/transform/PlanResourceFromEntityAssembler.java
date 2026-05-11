package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.PlanResource;

public class PlanResourceFromEntityAssembler {
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
