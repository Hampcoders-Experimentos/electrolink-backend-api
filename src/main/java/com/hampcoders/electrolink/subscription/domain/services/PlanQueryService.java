package com.hampcoders.electrolink.subscription.domain.services;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetAllPlansQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetPlanByIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import java.util.List;
import java.util.Optional;

public interface PlanQueryService {
    List<Plan> handle(GetAllPlansQuery query);
    Optional<Plan> handle(GetPlanByIdQuery query);
    Optional<Plan> findByType(PlanType type);
}
