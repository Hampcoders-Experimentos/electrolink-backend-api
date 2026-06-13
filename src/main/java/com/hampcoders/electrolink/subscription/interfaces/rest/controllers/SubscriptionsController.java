package com.hampcoders.electrolink.subscription.interfaces.rest.controllers;

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

/**
 * REST controller for managing subscriptions.
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionsController {

  private final SubscriptionCommandService subscriptionCommandService;
  private final SubscriptionQueryService subscriptionQueryService;

  /**
   * Constructor for SubscriptionsController.
   *
   * @param subscriptionCommandService the service for handling subscription commands
   * @param subscriptionQueryService the service for handling subscription queries
   */
  public SubscriptionsController(SubscriptionCommandService subscriptionCommandService,
                                 SubscriptionQueryService subscriptionQueryService) {
    this.subscriptionCommandService = subscriptionCommandService;
    this.subscriptionQueryService = subscriptionQueryService;
  }

  /**
   * Create a new subscription.
   *
   * @param resource the resource containing subscription details
   * @return ResponseEntity with the created subscription resource and HTTP status
   */
  @PostMapping
  public ResponseEntity<SubscriptionResource> createSubscription(
      @RequestBody CreateSubscriptionResource resource) {
    var command = CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);
    var subscription = subscriptionCommandService.handle(command);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription));
  }

  /**
   * Get subscription details by user ID.
   *
   * @param userId the ID of the user
   * @return ResponseEntity with the subscription resource and HTTP status
   */
  @GetMapping("/users/{userId}")
  public ResponseEntity<SubscriptionResource> getSubscriptionByUserId(@PathVariable Long userId) {
    return subscriptionQueryService.handle(new GetSubscriptionByUserIdQuery(userId))
        .map(sub -> ResponseEntity.ok(
            SubscriptionResourceFromEntityAssembler.toResourceFromEntity(sub)))
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Get the active subscription for a user by user ID.
   *
   * @param userId the ID of the user
   * @return ResponseEntity with the active subscription resource and HTTP status
   */
  @GetMapping("/users/{userId}/active")
  public ResponseEntity<SubscriptionResource> getActiveSubscription(@PathVariable Long userId) {
    return subscriptionQueryService.handle(new GetActiveSubscriptionByUserIdQuery(userId))
        .map(sub -> ResponseEntity.ok(
            SubscriptionResourceFromEntityAssembler.toResourceFromEntity(sub)))
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Upgrade a user's subscription to a new plan.
   *
   * @param userId the ID of the user
   * @param planId the ID of the new plan
   * @return ResponseEntity indicating the result of the operation
   */
  @PutMapping("/users/{userId}/upgrade/{planId}")
  public ResponseEntity<Void> upgradeSubscription(@PathVariable Long userId,
                                                  @PathVariable Long planId) {
    subscriptionCommandService.handle(new UpgradeSubscriptionCommand(userId, planId));
    return ResponseEntity.ok().build();
  }

  /**
   * Cancel a user's subscription.
   *
   * @param userId the ID of the user
   * @return ResponseEntity indicating the result of the operation
   */
  @DeleteMapping("/users/{userId}")
  public ResponseEntity<Void> cancelSubscription(@PathVariable Long userId) {
    subscriptionCommandService.handle(new CancelSubscriptionCommand(userId));
    return ResponseEntity.noContent().build();
  }
}
