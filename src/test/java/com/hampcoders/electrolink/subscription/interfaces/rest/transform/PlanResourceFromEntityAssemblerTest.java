package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanResourceFromEntityAssemblerTest {

    @Mock
    private Plan plan;

    @Test
    @DisplayName("toResourceFromEntity should map all fields (AAA)")
    void toResourceFromEntity_ShouldMapAllFields() {
        when(plan.getId()).thenReturn(1L);
        when(plan.getName()).thenReturn(PlanType.PREMIUM);
        when(plan.getDescription()).thenReturn("Premium plan");
        when(plan.getPrice()).thenReturn(Money.usd(29.99));
        when(plan.getMaxRequestsPerMonth()).thenReturn(100);
        when(plan.isPrioritySupport()).thenReturn(true);
        when(plan.isActive()).thenReturn(true);

        var resource = PlanResourceFromEntityAssembler.toResourceFromEntity(plan);

        assertEquals(1L, resource.id());
        assertEquals("PREMIUM", resource.name());
        assertEquals("Premium plan", resource.description());
        assertEquals(29.99, resource.price());
        assertEquals(100, resource.maxRequestsPerMonth());
        assertTrue(resource.prioritySupport());
        assertTrue(resource.isActive());
    }
}
