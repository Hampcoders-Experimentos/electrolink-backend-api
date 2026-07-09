package com.hampcoders.electrolink.subscription.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetAllPlansQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetPlanByIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlanQueryServiceImplTest {

  @Mock
  private PlanRepository planRepository;

  @InjectMocks
  private PlanQueryServiceImpl planQueryService;

  @Test
  @DisplayName("Given existing plans, when handling GetAllPlansQuery, then it returns all of them")
  void handle_ShouldReturnAllPlans_WhenQueryingAll() {
    // Arrange
    List<Plan> plans = List.of(mock(Plan.class), mock(Plan.class));
    when(planRepository.findAll()).thenReturn(plans);

    // Act
    List<Plan> result = planQueryService.handle(new GetAllPlansQuery());

    // Assert
    assertEquals(plans, result);
  }

  @Test
  @DisplayName("Given a missing id, when handling GetPlanByIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenPlanIdMissing() {
    // Arrange
    when(planRepository.findById(5L)).thenReturn(Optional.empty());

    // Act
    Optional<Plan> result = planQueryService.handle(new GetPlanByIdQuery(5L));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given an existing type, when finding by type, then it returns the plan")
  void handle_ShouldReturnPlan_WhenTypeExists() {
    // Arrange
    Plan plan = mock(Plan.class);
    when(planRepository.findByName(PlanType.PREMIUM)).thenReturn(Optional.of(plan));

    // Act
    Optional<Plan> result = planQueryService.findByType(PlanType.PREMIUM);

    // Assert
    assertTrue(result.isPresent());
    assertSame(plan, result.get());
  }
}
