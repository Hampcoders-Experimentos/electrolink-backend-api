package com.hampcoders.electrolink.subscription.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreatePlanCommand;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlanCommandServiceImplTest {

  @Mock
  private PlanRepository planRepository;

  @InjectMocks
  private PlanCommandServiceImpl planCommandService;

  @Test
  @DisplayName("Given a basic plan command, when handling CreatePlanCommand, then it returns the saved plan")
  void handle_ShouldReturnSavedPlan_WhenBasicPlanCreated() {
    // Arrange
    CreatePlanCommand command = new CreatePlanCommand(PlanType.BASIC, "desc", 9.99, 10, false);
    Plan saved = mock(Plan.class);
    when(planRepository.save(any(Plan.class))).thenReturn(saved);

    // Act
    Plan result = planCommandService.handle(command);

    // Assert
    assertSame(saved, result);
  }

  @Test
  @DisplayName("Given a premium plan command, when handling CreatePlanCommand, then it returns the saved plan")
  void handle_ShouldReturnSavedPlan_WhenPremiumPlanCreated() {
    // Arrange
    CreatePlanCommand command = new CreatePlanCommand(PlanType.PREMIUM, "desc", 19.99, 100, true);
    Plan saved = mock(Plan.class);
    when(planRepository.save(any(Plan.class))).thenReturn(saved);

    // Act
    Plan result = planCommandService.handle(command);

    // Assert
    assertSame(saved, result);
  }

  @Test
  @DisplayName("Given the repository fails, when handling CreatePlanCommand, then it propagates the exception")
  void handle_ShouldPropagateException_WhenSaveFails() {
    // Arrange
    CreatePlanCommand command = new CreatePlanCommand(PlanType.BASIC, "desc", 9.99, 10, false);
    when(planRepository.save(any(Plan.class))).thenThrow(new RuntimeException("DB error"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> planCommandService.handle(command));
  }
}
