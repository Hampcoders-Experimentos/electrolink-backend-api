package com.hampcoders.electrolink.subscription.application.internal.eventhandlers;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanSeedOnStartupTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanSeedOnStartup planSeedOnStartup;

    @Test
    @DisplayName("onApplicationReady should seed both plans when they don't exist (AAA)")
    void onApplicationReady_ShouldSeedBothPlans() {
        // Arrange
        when(planRepository.existsByName(PlanType.BASIC)).thenReturn(false);
        when(planRepository.existsByName(PlanType.PREMIUM)).thenReturn(false);

        // Act
        planSeedOnStartup.onApplicationReady();

        // Assert
        verify(planRepository).existsByName(PlanType.BASIC);
        verify(planRepository).existsByName(PlanType.PREMIUM);
        // Debería guardarse exactamente 2 veces (uno por BASIC y otro por PREMIUM)
        verify(planRepository, times(2)).save(any(Plan.class));
    }

    @Test
    @DisplayName("onApplicationReady should not seed when plans already exist (AAA)")
    void onApplicationReady_ShouldNotSeed_WhenPlansExist() {
        // Arrange
        when(planRepository.existsByName(PlanType.BASIC)).thenReturn(true);
        when(planRepository.existsByName(PlanType.PREMIUM)).thenReturn(true);

        // Act
        planSeedOnStartup.onApplicationReady();

        // Assert
        verify(planRepository).existsByName(PlanType.BASIC);
        verify(planRepository).existsByName(PlanType.PREMIUM);
        // No debería guardarse nada porque ya existen
        verify(planRepository, never()).save(any(Plan.class));
    }
}
