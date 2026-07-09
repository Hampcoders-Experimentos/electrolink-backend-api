package com.hampcoders.electrolink.subscription.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.RecordRequestCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.UpgradeSubscriptionCommand;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionCommandServiceImplTest {

  @Mock
  private SubscriptionRepository subscriptionRepository;
  @Mock
  private PlanRepository planRepository;

  @InjectMocks
  private SubscriptionCommandServiceImpl subscriptionCommandService;

  @Test
  @DisplayName("Given a user without a subscription, when handling CreateSubscriptionCommand, then it creates one")
  void handle_ShouldCreateSubscription_WhenUserHasNone() {
    // Arrange
    CreateSubscriptionCommand command = new CreateSubscriptionCommand(2L, 1L);
    when(planRepository.findById(1L)).thenReturn(Optional.of(mock(Plan.class)));
    when(subscriptionRepository.findByUserId(2L)).thenReturn(Optional.empty());
    Subscription saved = mock(Subscription.class);
    when(subscriptionRepository.save(any(Subscription.class))).thenReturn(saved);

    // Act
    Subscription result = subscriptionCommandService.handle(command);

    // Assert
    assertSame(saved, result);
  }

  @Test
  @DisplayName("Given a missing plan, when handling CreateSubscriptionCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenPlanNotFound() {
    // Arrange
    CreateSubscriptionCommand command = new CreateSubscriptionCommand(2L, 1L);
    when(planRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> subscriptionCommandService.handle(command));
  }

  @Test
  @DisplayName("Given an existing subscription, when handling CreateSubscriptionCommand, then it upgrades it")
  void handle_ShouldUpgradeSubscription_WhenUserAlreadySubscribed() {
    // Arrange
    CreateSubscriptionCommand command = new CreateSubscriptionCommand(2L, 1L);
    Plan plan = mock(Plan.class);
    Subscription existing = mock(Subscription.class);
    when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
    when(subscriptionRepository.findByUserId(2L)).thenReturn(Optional.of(existing));
    when(subscriptionRepository.save(existing)).thenReturn(existing);

    // Act
    Subscription result = subscriptionCommandService.handle(command);

    // Assert
    assertSame(existing, result);
    verify(existing).upgradeTo(plan);
  }

  @Test
  @DisplayName("Given a valid upgrade, when handling UpgradeSubscriptionCommand, then it upgrades and saves")
  void handle_ShouldUpgrade_WhenPlanAndSubscriptionExist() {
    // Arrange
    UpgradeSubscriptionCommand command = new UpgradeSubscriptionCommand(2L, 3L);
    Plan plan = mock(Plan.class);
    Subscription subscription = mock(Subscription.class);
    when(planRepository.findById(3L)).thenReturn(Optional.of(plan));
    when(subscriptionRepository.findByUserId(2L)).thenReturn(Optional.of(subscription));
    when(subscriptionRepository.save(subscription)).thenReturn(subscription);

    // Act
    Subscription result = subscriptionCommandService.handle(command);

    // Assert
    assertSame(subscription, result);
    verify(subscription).upgradeTo(plan);
  }

  @Test
  @DisplayName("Given a missing plan, when handling UpgradeSubscriptionCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenPlanMissingOnUpgrade() {
    // Arrange
    UpgradeSubscriptionCommand command = new UpgradeSubscriptionCommand(2L, 3L);
    when(planRepository.findById(3L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> subscriptionCommandService.handle(command));
  }

  @Test
  @DisplayName("Given no subscription, when handling UpgradeSubscriptionCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenSubscriptionMissingOnUpgrade() {
    // Arrange
    UpgradeSubscriptionCommand command = new UpgradeSubscriptionCommand(2L, 3L);
    when(planRepository.findById(3L)).thenReturn(Optional.of(mock(Plan.class)));
    when(subscriptionRepository.findByUserId(2L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> subscriptionCommandService.handle(command));
  }

  @Test
  @DisplayName("Given an existing subscription, when handling CancelSubscriptionCommand, then it cancels and saves")
  void handle_ShouldCancel_WhenSubscriptionExists() {
    // Arrange
    CancelSubscriptionCommand command = new CancelSubscriptionCommand(2L);
    Subscription subscription = mock(Subscription.class);
    when(subscriptionRepository.findByUserId(2L)).thenReturn(Optional.of(subscription));

    // Act
    subscriptionCommandService.handle(command);

    // Assert
    verify(subscription).cancel();
    verify(subscriptionRepository).save(subscription);
  }

  @Test
  @DisplayName("Given no subscription, when handling CancelSubscriptionCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenSubscriptionMissingOnCancel() {
    // Arrange
    CancelSubscriptionCommand command = new CancelSubscriptionCommand(2L);
    when(subscriptionRepository.findByUserId(2L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> subscriptionCommandService.handle(command));
  }

  @Test
  @DisplayName("Given an existing subscription, when handling RecordRequestCommand, then it records and saves")
  void handle_ShouldRecordRequest_WhenSubscriptionExists() {
    // Arrange
    RecordRequestCommand command = new RecordRequestCommand(2L);
    Subscription subscription = mock(Subscription.class);
    when(subscriptionRepository.findByUserId(2L)).thenReturn(Optional.of(subscription));
    when(subscriptionRepository.save(subscription)).thenReturn(subscription);

    // Act
    Subscription result = subscriptionCommandService.handle(command);

    // Assert
    assertSame(subscription, result);
    verify(subscription).recordRequest();
  }

  @Test
  @DisplayName("Given no subscription, when handling RecordRequestCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenSubscriptionMissingOnRecordRequest() {
    // Arrange
    RecordRequestCommand command = new RecordRequestCommand(2L);
    when(subscriptionRepository.findByUserId(2L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> subscriptionCommandService.handle(command));
  }
}
