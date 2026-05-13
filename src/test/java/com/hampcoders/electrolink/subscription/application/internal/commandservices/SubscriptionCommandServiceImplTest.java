package com.hampcoders.electrolink.subscription.application.internal.commandservices;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.RecordRequestCommand;
import com.hampcoders.electrolink.subscription.domain.model.commands.UpgradeSubscriptionCommand;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionCommandServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private PlanRepository planRepository;
    @InjectMocks
    private SubscriptionCommandServiceImpl subscriptionCommandService;

    private final Long USER_ID = 1L;
    private final Long PLAN_ID = 2L;

    @Test
    @DisplayName("handle(CreateSubscriptionCommand) should create subscription when no existing subscription (AAA)")
    void handleCreateCommand_ShouldCreate_WhenNoExisting() {
        var plan = mock(Plan.class);
        var command = new CreateSubscriptionCommand(USER_ID, PLAN_ID);

        when(planRepository.findById(PLAN_ID)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));

        var result = subscriptionCommandService.handle(command);

        assertNotNull(result);
        verify(planRepository).findById(PLAN_ID);
        verify(subscriptionRepository).findByUserId(USER_ID);
        verify(subscriptionRepository).save(any(Subscription.class));
        verifyNoMoreInteractions(planRepository, subscriptionRepository);
    }

    @Test
    @DisplayName("handle(CreateSubscriptionCommand) should upgrade existing subscription when already exists (AAA)")
    void handleCreateCommand_ShouldUpgrade_WhenExisting() {
        var plan = mock(Plan.class);
        var existingSubscription = mock(Subscription.class);
        var command = new CreateSubscriptionCommand(USER_ID, PLAN_ID);

        when(planRepository.findById(PLAN_ID)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingSubscription));
        when(subscriptionRepository.save(existingSubscription)).thenReturn(existingSubscription);

        var result = subscriptionCommandService.handle(command);

        assertNotNull(result);
        verify(existingSubscription).upgradeTo(plan);
        verify(subscriptionRepository).save(existingSubscription);
        verify(planRepository).findById(PLAN_ID);
        verify(subscriptionRepository).findByUserId(USER_ID);
        verifyNoMoreInteractions(planRepository, subscriptionRepository);
    }

    @Test
    @DisplayName("handle(CreateSubscriptionCommand) should throw when plan not found (AAA)")
    void handleCreateCommand_ShouldThrow_WhenPlanNotFound() {
        var command = new CreateSubscriptionCommand(USER_ID, PLAN_ID);
        when(planRepository.findById(PLAN_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> subscriptionCommandService.handle(command));

        verify(planRepository).findById(PLAN_ID);
        verifyNoMoreInteractions(planRepository);
        verifyNoInteractions(subscriptionRepository);
    }

    @Test
    @DisplayName("handle(UpgradeSubscriptionCommand) should upgrade subscription (AAA)")
    void handleUpgradeCommand_ShouldUpgrade() {
        var newPlan = mock(Plan.class);
        var existingSubscription = mock(Subscription.class);
        var command = new UpgradeSubscriptionCommand(USER_ID, PLAN_ID);

        when(planRepository.findById(PLAN_ID)).thenReturn(Optional.of(newPlan));
        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingSubscription));
        when(subscriptionRepository.save(existingSubscription)).thenReturn(existingSubscription);

        var result = subscriptionCommandService.handle(command);

        assertNotNull(result);
        verify(existingSubscription).upgradeTo(newPlan);
        verify(subscriptionRepository).save(existingSubscription);
        verify(planRepository).findById(PLAN_ID);
        verify(subscriptionRepository).findByUserId(USER_ID);
        verifyNoMoreInteractions(planRepository, subscriptionRepository);
    }

    @Test
    @DisplayName("handle(CancelSubscriptionCommand) should cancel subscription (AAA)")
    void handleCancelCommand_ShouldCancel() {
        var subscription = mock(Subscription.class);
        var command = new CancelSubscriptionCommand(USER_ID);

        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.of(subscription));

        subscriptionCommandService.handle(command);

        verify(subscription).cancel();
        verify(subscriptionRepository).save(subscription);
        verify(subscriptionRepository).findByUserId(USER_ID);
        verifyNoMoreInteractions(subscriptionRepository);
        verifyNoInteractions(planRepository);
    }

    @Test
    @DisplayName("handle(RecordRequestCommand) should record request on subscription (AAA)")
    void handleRecordRequestCommand_ShouldRecord() {
        var subscription = mock(Subscription.class);
        var command = new RecordRequestCommand(USER_ID);

        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.save(subscription)).thenReturn(subscription);

        var result = subscriptionCommandService.handle(command);

        assertNotNull(result);
        verify(subscription).recordRequest();
        verify(subscriptionRepository).save(subscription);
        verify(subscriptionRepository).findByUserId(USER_ID);
        verifyNoMoreInteractions(subscriptionRepository);
        verifyNoInteractions(planRepository);
    }
}
