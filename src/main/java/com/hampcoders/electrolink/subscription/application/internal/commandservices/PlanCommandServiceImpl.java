package com.hampcoders.electrolink.subscription.application.internal.commandservices;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreatePlanCommand;
import com.hampcoders.electrolink.subscription.domain.services.PlanCommandService;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the PlanCommandService interface,
 * responsible for handling commands related to Plan entities.
 */
@Service
public class PlanCommandServiceImpl implements PlanCommandService {

  private final PlanRepository planRepository;

  /**
   * Constructor for PlanCommandServiceImpl.
   *
   * @param planRepository the repository used for Plan persistence operations
   */
  public PlanCommandServiceImpl(PlanRepository planRepository) {
    this.planRepository = planRepository;
  }

  /**
   * Handles the CreatePlanCommand to create a new Plan entity and save it to the database.
   *
   * @param command the command containing the details for creating the plan
   * @return the created Plan entity
   */
  @Override
  @Transactional
  public Plan handle(CreatePlanCommand command) {
    var plan = new Plan(command);
    return planRepository.save(plan);
  }
}
