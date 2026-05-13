package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import com.hampcoders.electrolink.subscription.interfaces.rest.resources.CreateSubscriptionResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionCommandFromResourceAssemblerTest {

    @Test
    @DisplayName("toCommandFromResource should map all fields (AAA)")
    void toCommandFromResource_ShouldMapAllFields() {
        var resource = new CreateSubscriptionResource(1L, 2L);

        var command = CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);

        assertEquals(1L, command.userId());
        assertEquals(2L, command.planId());
    }
}
