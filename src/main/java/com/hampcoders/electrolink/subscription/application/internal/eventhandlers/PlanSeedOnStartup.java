package com.hampcoders.electrolink.subscription.application.internal.eventhandlers;

import com.hampcoders.electrolink.subscription.domain.services.PlanCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PlanSeedOnStartup {

    private static final Logger log = LoggerFactory.getLogger(PlanSeedOnStartup.class);
    private final PlanCommandService planCommandService;

    public PlanSeedOnStartup(PlanCommandService planCommandService) {
        this.planCommandService = planCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Seeding default plans...");
        planCommandService.seedDefaultPlans();
        log.info("Default plans seeded successfully.");
    }
}
