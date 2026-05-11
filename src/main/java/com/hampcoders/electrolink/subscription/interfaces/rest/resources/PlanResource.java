package com.hampcoders.electrolink.subscription.interfaces.rest.resources;

public record PlanResource(Long id, String name, String description, double price,
                           int maxRequestsPerMonth, boolean prioritySupport, boolean isActive) {
}
