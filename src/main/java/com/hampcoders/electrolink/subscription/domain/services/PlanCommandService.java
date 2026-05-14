package com.hampcoders.electrolink.subscription.domain.services;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreatePlanCommand;

/**
 * Service interface for handling commands related to Plan entities.
 */
public interface PlanCommandService {

  /**
   * Handles the creation of a new Plan based on the provided command.
   *
   * @param command the command containing the details for creating the plan
   * @return the newly created Plan
   */
  Plan handle(CreatePlanCommand command);
}
