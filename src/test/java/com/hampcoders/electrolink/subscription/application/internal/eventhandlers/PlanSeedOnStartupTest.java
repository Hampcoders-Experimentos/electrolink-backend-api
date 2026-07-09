package com.hampcoders.electrolink.subscription.application.internal.eventhandlers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlanSeedOnStartupTest {

  @Mock
  private PlanRepository planRepository;

  @InjectMocks
  private PlanSeedOnStartup planSeedOnStartup;

  @Test
  @DisplayName("Given no default plans exist, when the application is ready, then it seeds both plans")
  void handle_ShouldSeedBothPlans_WhenNoneExist() {
    // Arrange
    when(planRepository.existsByName(PlanType.BASIC)).thenReturn(false);
    when(planRepository.existsByName(PlanType.PREMIUM)).thenReturn(false);

    // Act
    planSeedOnStartup.onApplicationReady();

    // Assert
    verify(planRepository, times(2)).save(any(Plan.class));
  }

  @Test
  @DisplayName("Given all default plans exist, when the application is ready, then it seeds none")
  void handle_ShouldSeedNothing_WhenAllPlansExist() {
    // Arrange
    when(planRepository.existsByName(PlanType.BASIC)).thenReturn(true);
    when(planRepository.existsByName(PlanType.PREMIUM)).thenReturn(true);

    // Act
    planSeedOnStartup.onApplicationReady();

    // Assert
    verify(planRepository, never()).save(any(Plan.class));
  }

  @Test
  @DisplayName("Given the repository fails to save, when the application is ready, then it propagates the exception")
  void handle_ShouldPropagateException_WhenSaveFails() {
    // Arrange
    when(planRepository.existsByName(PlanType.BASIC)).thenReturn(false);
    when(planRepository.save(any(Plan.class))).thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> planSeedOnStartup.onApplicationReady());
  }
}
