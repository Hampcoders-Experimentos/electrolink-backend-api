package com.hampcoders.electrolink.iam.domain.model.events;

/**
 * Event representing the registration of a new user in the system.
 *
 * @param userId the user id
 * @param username the name of the user
 */
public record UserRegisteredEvent(Long userId, String username) {
}
