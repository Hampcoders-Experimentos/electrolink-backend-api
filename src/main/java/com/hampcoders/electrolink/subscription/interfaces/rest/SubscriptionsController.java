package com.hampcoders.electrolink.subscription.interfaces.rest;

import com.hampcoders.electrolink.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.UpgradeSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetActiveSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionCommandService;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionQueryService;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.CreateSubscriptionResource;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.SubscriptionResource;
import com.hampcoders.electrolink.subscription.interfaces.rest.transform.CreateSubscriptionCommandFromResourceAssembler;
import com.hampcoders.electrolink.subscription.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionsController {

    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;

    public SubscriptionsController(SubscriptionCommandService subscriptionCommandService,
                                   SubscriptionQueryService subscriptionQueryService) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.subscriptionQueryService = subscriptionQueryService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResource> createSubscription(
            @RequestBody CreateSubscriptionResource resource) {
        var command = CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);
        var subscription = subscriptionCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<SubscriptionResource> getSubscriptionByUserId(@PathVariable Long userId) {
        return subscriptionQueryService.handle(new GetSubscriptionByUserIdQuery(userId))
            .map(sub -> ResponseEntity.ok(SubscriptionResourceFromEntityAssembler.toResourceFromEntity(sub)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/active")
    public ResponseEntity<SubscriptionResource> getActiveSubscription(@PathVariable Long userId) {
        return subscriptionQueryService.handle(new GetActiveSubscriptionByUserIdQuery(userId))
            .map(sub -> ResponseEntity.ok(SubscriptionResourceFromEntityAssembler.toResourceFromEntity(sub)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{userId}/upgrade/{planId}")
    public ResponseEntity<Void> upgradeSubscription(@PathVariable Long userId,
                                                     @PathVariable Long planId) {
        subscriptionCommandService.handle(new UpgradeSubscriptionCommand(userId, planId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long userId) {
        subscriptionCommandService.handle(new CancelSubscriptionCommand(userId));
        return ResponseEntity.noContent().build();
    }
}
