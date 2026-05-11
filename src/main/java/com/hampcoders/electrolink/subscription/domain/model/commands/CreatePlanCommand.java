package com.hampcoders.electrolink.subscription.domain.model.commands;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;

public record CreatePlanCommand(PlanType name, String description, double price,
                                int maxRequestsPerMonth, boolean prioritySupport) {
}
