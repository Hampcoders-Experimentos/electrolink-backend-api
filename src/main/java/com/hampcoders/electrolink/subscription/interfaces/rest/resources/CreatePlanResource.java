package com.hampcoders.electrolink.subscription.interfaces.rest.resources;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;

public record CreatePlanResource(PlanType name, String description, double price,
                                 int maxRequestsPerMonth, boolean prioritySupport) {
}
