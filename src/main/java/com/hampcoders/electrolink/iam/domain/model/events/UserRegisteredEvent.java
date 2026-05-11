package com.hampcoders.electrolink.iam.domain.model.events;

public record UserRegisteredEvent(Long userId, String username) {
}
