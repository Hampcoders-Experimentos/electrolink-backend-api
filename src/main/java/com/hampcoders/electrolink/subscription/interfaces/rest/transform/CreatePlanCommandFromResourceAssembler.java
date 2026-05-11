package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import com.hampcoders.electrolink.subscription.domain.model.commands.CreatePlanCommand;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.CreatePlanResource;

public class CreatePlanCommandFromResourceAssembler {
    public static CreatePlanCommand toCommandFromResource(CreatePlanResource resource) {
        return new CreatePlanCommand(
            resource.name(),
            resource.description(),
            resource.price(),
            resource.maxRequestsPerMonth(),
            resource.prioritySupport()
        );
    }
}
