package com.hampcoders.electrolink.subscription.domain.model.commands;

/**
 * Command to record a subscription request for a user.
 *
 * @param userId The ID of the user making the subscription request.
 */
public record RecordRequestCommand(Long userId) {
}
