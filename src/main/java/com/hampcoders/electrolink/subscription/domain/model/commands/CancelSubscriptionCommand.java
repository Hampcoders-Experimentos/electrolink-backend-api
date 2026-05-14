package com.hampcoders.electrolink.subscription.domain.model.commands;

/**
 * Command object representing the data needed to cancel a user's subscription.
 *
 * @param userId the ID of the user whose subscription is to be canceled
 */
public record CancelSubscriptionCommand(Long userId) {
}
