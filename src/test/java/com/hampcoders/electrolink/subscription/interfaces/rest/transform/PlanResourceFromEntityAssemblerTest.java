package com.hampcoders.electrolink.subscription.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.Money;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.PlanResource;
import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlanResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a basic plan, when assembling, then it maps all fields")
  void handle_ShouldMapAllFields_WhenPlanProvided() {
    // Arrange
    Plan plan = mock(Plan.class);
    when(plan.getId()).thenReturn(1L);
    when(plan.getName()).thenReturn(PlanType.BASIC);
    when(plan.getDescription()).thenReturn("desc");
    when(plan.getPrice()).thenReturn(new Money(new BigDecimal("9.99"), Currency.getInstance("USD")));
    when(plan.getMaxRequestsPerMonth()).thenReturn(10);
    when(plan.isPrioritySupport()).thenReturn(false);
    when(plan.isActive()).thenReturn(true);

    // Act
    PlanResource resource = PlanResourceFromEntityAssembler.toResourceFromEntity(plan);

    // Assert
    assertEquals(1L, resource.id());
    assertEquals("BASIC", resource.name());
    assertEquals("desc", resource.description());
    assertEquals(9.99, resource.price());
    assertEquals(10, resource.maxRequestsPerMonth());
    assertFalse(resource.prioritySupport());
    assertTrue(resource.isActive());
  }

  @Test
  @DisplayName("Given a premium plan, when assembling, then it maps the premium values")
  void handle_ShouldMapPremiumValues_WhenPlanIsPremium() {
    // Arrange
    Plan plan = mock(Plan.class);
    when(plan.getId()).thenReturn(2L);
    when(plan.getName()).thenReturn(PlanType.PREMIUM);
    when(plan.getDescription()).thenReturn("premium");
    when(plan.getPrice())
        .thenReturn(new Money(new BigDecimal("19.99"), Currency.getInstance("USD")));
    when(plan.getMaxRequestsPerMonth()).thenReturn(100);
    when(plan.isPrioritySupport()).thenReturn(true);
    when(plan.isActive()).thenReturn(true);

    // Act
    PlanResource resource = PlanResourceFromEntityAssembler.toResourceFromEntity(plan);

    // Assert
    assertEquals("PREMIUM", resource.name());
    assertEquals(19.99, resource.price());
    assertTrue(resource.prioritySupport());
  }

  @Test
  @DisplayName("Given a null plan, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenPlanIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> PlanResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
