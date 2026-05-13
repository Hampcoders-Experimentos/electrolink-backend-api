package com.hampcoders.electrolink.subscription.interfaces.acl;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.RecordRequestCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.UpgradeSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetActiveSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionCommandService;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionQueryService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionContextFacade {

    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;

    public SubscriptionContextFacade(SubscriptionCommandService subscriptionCommandService,
                                     SubscriptionQueryService subscriptionQueryService) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.subscriptionQueryService = subscriptionQueryService;
    }

    public Long createSubscription(Long userId, Long planId) {
        var command = new CreateSubscriptionCommand(userId, planId);
        return subscriptionCommandService.handle(command).getId();
    }

    public void upgradeSubscription(Long userId, Long newPlanId) {
        subscriptionCommandService.handle(new UpgradeSubscriptionCommand(userId, newPlanId));
    }

    public void cancelSubscription(Long userId) {
        subscriptionCommandService.handle(new CancelSubscriptionCommand(userId));
    }

    public boolean canUserMakeRequest(Long userId) {
        return subscriptionQueryService.canUserMakeRequest(userId);
    }

    public void recordRequest(Long userId) {
        subscriptionCommandService.handle(new RecordRequestCommand(userId));
    }

    public Optional<Subscription> getActiveSubscription(Long userId) {
        return subscriptionQueryService.handle(new GetActiveSubscriptionByUserIdQuery(userId));
    }

    public Optional<Long> getActiveSubscriptionPlanId(Long userId) {
        return subscriptionQueryService.handle(new GetActiveSubscriptionByUserIdQuery(userId))
            .map(s -> s.getPlan().getId());
    }

    public boolean isPremiumUser(Long userId) {
        return subscriptionQueryService.handle(new GetActiveSubscriptionByUserIdQuery(userId))
            .map(Subscription::isPremium)
            .orElse(false);
    }
}
