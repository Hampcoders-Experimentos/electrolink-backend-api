package com.hampcoders.electrolink.subscription.application.internal.queryservices;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetAllPlansQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetPlanByIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanQueryServiceImplTest {

    @Mock
    private PlanRepository planRepository;
    @InjectMocks
    private PlanQueryServiceImpl planQueryService;

    private final Long PLAN_ID = 1L;

    @Test
    @DisplayName("handle(GetAllPlansQuery) should return all plans (AAA)")
    void handleGetAllPlans_ShouldReturnAll() {
        var plan1 = mock(Plan.class);
        var plan2 = mock(Plan.class);
        var query = new GetAllPlansQuery();

        when(planRepository.findAll()).thenReturn(List.of(plan1, plan2));

        var result = planQueryService.handle(query);

        assertEquals(2, result.size());
        verify(planRepository).findAll();
        verifyNoMoreInteractions(planRepository);
    }

    @Test
    @DisplayName("handle(GetPlanByIdQuery) should return plan when found (AAA)")
    void handleGetPlanById_ShouldReturn_WhenFound() {
        var plan = mock(Plan.class);
        var query = new GetPlanByIdQuery(PLAN_ID);

        when(planRepository.findById(PLAN_ID)).thenReturn(Optional.of(plan));

        var result = planQueryService.handle(query);

        assertTrue(result.isPresent());
        assertEquals(plan, result.get());
        verify(planRepository).findById(PLAN_ID);
        verifyNoMoreInteractions(planRepository);
    }

    @Test
    @DisplayName("handle(GetPlanByIdQuery) should return empty when not found (AAA)")
    void handleGetPlanById_ShouldReturnEmpty_WhenNotFound() {
        var query = new GetPlanByIdQuery(PLAN_ID);

        when(planRepository.findById(PLAN_ID)).thenReturn(Optional.empty());

        var result = planQueryService.handle(query);

        assertTrue(result.isEmpty());
        verify(planRepository).findById(PLAN_ID);
        verifyNoMoreInteractions(planRepository);
    }

    @Test
    @DisplayName("findByType should return plan when found (AAA)")
    void findByType_ShouldReturn_WhenFound() {
        var plan = mock(Plan.class);

        when(planRepository.findByName(PlanType.PREMIUM)).thenReturn(Optional.of(plan));

        var result = planQueryService.findByType(PlanType.PREMIUM);

        assertTrue(result.isPresent());
        assertEquals(plan, result.get());
        verify(planRepository).findByName(PlanType.PREMIUM);
        verifyNoMoreInteractions(planRepository);
    }

    @Test
    @DisplayName("findByType should return empty when not found (AAA)")
    void findByType_ShouldReturnEmpty_WhenNotFound() {
        when(planRepository.findByName(PlanType.BASIC)).thenReturn(Optional.empty());

        var result = planQueryService.findByType(PlanType.BASIC);

        assertTrue(result.isEmpty());
        verify(planRepository).findByName(PlanType.BASIC);
        verifyNoMoreInteractions(planRepository);
    }
}
