package com.hampcoders.electrolink.subscription.domain.services;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreatePlanCommand;
import java.util.Optional;

public interface PlanCommandService {
    Plan handle(CreatePlanCommand command);
    void seedDefaultPlans();
    Optional<Plan> findById(Long planId);
}
