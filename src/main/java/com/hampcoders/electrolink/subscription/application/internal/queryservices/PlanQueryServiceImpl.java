package com.hampcoders.electrolink.subscription.application.internal.queryservices;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetAllPlansQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetPlanByIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.domain.services.PlanQueryService;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the PlanQueryService interface
 * that handles queries related to subscription plans.
 */
@Service
public class PlanQueryServiceImpl implements PlanQueryService {

  private final PlanRepository planRepository;

  /**
   * Constructor for PlanQueryServiceImpl.
   *
   * @param planRepository the repository used to access plan data from the database
   */
  public PlanQueryServiceImpl(PlanRepository planRepository) {
    this.planRepository = planRepository;
  }

  /**
   * Handles the GetAllPlansQuery by retrieving all subscription plans from the database.
   *
   * @param query The query object containing any necessary parameters for retrieving the plans.
   * @return A list of Plan objects representing all available subscription plans.
   */
  @Override
  public List<Plan> handle(GetAllPlansQuery query) {
    return planRepository.findAll();
  }

  /**
   * Handles the GetPlanByIdQuery by retrieving a specific subscription plan from the database.
   *
   * @param query The query object containing the unique identifier
   *     of the subscription plan to be retrieved.
   * @return An Optional containing the Plan object if found,
   *     or an empty Optional if no plan with the specified ID exists.
   */
  @Override
  public Optional<Plan> handle(GetPlanByIdQuery query) {
    return planRepository.findById(query.planId());
  }

  /**
   * Retrieves a subscription plan based on its type from the database.
   *
   * @param type The PlanType enum value representing the type of subscription plan to be retrieved.
   * @return An Optional containing the Plan object if found,
   */
  @Override
  public Optional<Plan> findByType(PlanType type) {
    return planRepository.findByName(type);
  }
}
