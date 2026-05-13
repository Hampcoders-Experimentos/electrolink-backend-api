package com.hampcoders.electrolink.subscription.application.internal.commandservices;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.RecordRequestCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.UpgradeSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionCommandService;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;

    public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository,
                                          PlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
    }

    @Override
    @Transactional
    public Subscription handle(CreateSubscriptionCommand command) {
        var plan = planRepository.findById(command.planId())
            .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + command.planId()));

        var existing = subscriptionRepository.findByUserId(command.userId());
        if (existing.isPresent()) {
            var sub = existing.get();
            sub.upgradeTo(plan);
            return subscriptionRepository.save(sub);
        }

        var subscription = new Subscription(command, plan);
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription handle(UpgradeSubscriptionCommand command) {
        var newPlan = planRepository.findById(command.newPlanId())
            .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + command.newPlanId()));

        var subscription = subscriptionRepository.findByUserId(command.userId())
            .orElseThrow(() -> new IllegalArgumentException("No subscription found for user: " + command.userId()));

        subscription.upgradeTo(newPlan);
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void handle(CancelSubscriptionCommand command) {
        var subscription = subscriptionRepository.findByUserId(command.userId())
            .orElseThrow(() -> new IllegalArgumentException("No subscription found for user: " + command.userId()));

        subscription.cancel();
        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription handle(RecordRequestCommand command) {
        var subscription = subscriptionRepository.findByUserId(command.userId())
            .orElseThrow(() -> new IllegalArgumentException(
                "No subscription found for user: " + command.userId()));
        subscription.recordRequest();
        return subscriptionRepository.save(subscription);
    }
}
