package com.hampcoders.electrolink.subscription.domain.services;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetAllPlansQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetPlanByIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling queries related to subscription plans.
 */
public interface PlanQueryService {

  /**
   * Handles the GetAllPlansQuery to retrieve a list of all subscription plans.
   *
   * @param query The query object containing any necessary parameters for retrieving the plans.
   * @return A list of Plan objects representing all available subscription plans.
   */
  List<Plan> handle(GetAllPlansQuery query);

  /**
   * Handles the GetPlanByIdQuery to retrieve a specific subscription plan by its unique identifier.
   *
   * @param query The query object containing the unique identifier
   *     of the subscription plan to be retrieved.
   * @return An Optional containing the Plan object if found,
   *     or an empty Optional if no plan with the given identifier exists.
   */
  Optional<Plan> handle(GetPlanByIdQuery query);

  /**
   * Retrieves a subscription plan based on its type.
   *
   * @param type The PlanType enum value representing the type of subscription plan to be retrieved.
   * @return An Optional containing the Plan object if found,
   */
  Optional<Plan> findByType(PlanType type);
}
