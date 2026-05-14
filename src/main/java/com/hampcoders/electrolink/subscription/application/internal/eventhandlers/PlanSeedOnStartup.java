package com.hampcoders.electrolink.subscription.application.internal.eventhandlers;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This component listens for the ApplicationReadyEvent
 * and seeds the database with default plans if they do not already exist.
 */
@Component
public class PlanSeedOnStartup {

  private static final Logger log = LoggerFactory.getLogger(PlanSeedOnStartup.class);
  private final PlanRepository planRepository;

  /**
   * Constructor for PlanSeedOnStartup.
   *
   * @param planRepository the repository used to access and manipulate Plan entities
   */
  public PlanSeedOnStartup(PlanRepository planRepository) {
    this.planRepository = planRepository;
  }

  /**
   * This method is triggered when the application is ready.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    log.info("Seeding default plans...");
    if (!planRepository.existsByName(PlanType.BASIC)) {
      planRepository.save(Plan.createBasicPlan());
    }
    if (!planRepository.existsByName(PlanType.PREMIUM)) {
      planRepository.save(Plan.createPremiumPlan());
    }
    log.info("Default plans seeded successfully.");
  }
}
