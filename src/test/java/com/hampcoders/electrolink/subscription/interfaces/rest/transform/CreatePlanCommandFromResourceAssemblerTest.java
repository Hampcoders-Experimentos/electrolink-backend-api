package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.CreatePlanResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePlanCommandFromResourceAssemblerTest {

    @Test
    @DisplayName("toCommandFromResource should map all fields (AAA)")
    void toCommandFromResource_ShouldMapAllFields() {
        var resource = new CreatePlanResource(PlanType.BASIC, "Basic plan", 0.0, 5, false);

        var command = CreatePlanCommandFromResourceAssembler.toCommandFromResource(resource);

        assertEquals(PlanType.BASIC, command.name());
        assertEquals("Basic plan", command.description());
        assertEquals(0.0, command.price());
        assertEquals(5, command.maxRequestsPerMonth());
        assertFalse(command.prioritySupport());
    }
}
