package com.hampcoders.electrolink.subscription.application.internal.eventhandlers;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PlanSeedOnStartup {

    private static final Logger log = LoggerFactory.getLogger(PlanSeedOnStartup.class);
    private final PlanRepository planRepository;

    public PlanSeedOnStartup(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

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
