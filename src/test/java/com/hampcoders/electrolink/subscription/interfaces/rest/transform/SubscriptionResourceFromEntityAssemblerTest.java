package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionResourceFromEntityAssemblerTest {

    @Mock
    private Subscription subscription;
    @Mock
    private Plan plan;

    @Test
    @DisplayName("toResourceFromEntity should map all fields (AAA)")
    void toResourceFromEntity_ShouldMapAllFields() {
        var startDate = LocalDateTime.of(2026, 1, 1, 0, 0);

        when(subscription.getId()).thenReturn(1L);
        when(subscription.getUserId()).thenReturn(10L);
        when(subscription.getPlan()).thenReturn(plan);
        when(plan.getId()).thenReturn(5L);
        when(plan.getName()).thenReturn(PlanType.PREMIUM);
        when(subscription.getStatus()).thenReturn(com.hampcoders.electrolink.subscription.domain.model.valueobjects.SubscriptionStatus.ACTIVE);
        when(subscription.getStartDate()).thenReturn(startDate);
        when(subscription.getMonthlyRequestCount()).thenReturn(3);
        when(subscription.canMakeRequest()).thenReturn(true);

        var resource = SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription);

        assertEquals(1L, resource.id());
        assertEquals(10L, resource.userId());
        assertEquals(5L, resource.planId());
        assertEquals("PREMIUM", resource.planName());
        assertEquals("ACTIVE", resource.status());
        assertEquals(startDate, resource.startDate());
        assertEquals(3, resource.monthlyRequestCount());
        assertTrue(resource.canMakeRequest());
    }
}
