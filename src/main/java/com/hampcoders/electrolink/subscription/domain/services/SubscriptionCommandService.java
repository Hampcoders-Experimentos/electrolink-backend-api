package com.hampcoders.electrolink.subscription.domain.services;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.RecordRequestCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.UpgradeSubscriptionCommand;

public interface SubscriptionCommandService {
    Subscription handle(CreateSubscriptionCommand command);
    Subscription handle(UpgradeSubscriptionCommand command);
    void handle(CancelSubscriptionCommand command);
    Subscription handle(RecordRequestCommand command);
}
