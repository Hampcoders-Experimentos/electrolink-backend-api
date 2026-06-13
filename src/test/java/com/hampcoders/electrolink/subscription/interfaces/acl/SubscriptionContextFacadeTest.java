package com.hampcoders.electrolink.subscription.interfaces.acl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreateSubscriptionCommand;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetActiveSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionCommandService;
import com.hampcoders.electrolink.subscription.domain.services.SubscriptionQueryService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionContextFacadeTest {

  @Mock
  private SubscriptionCommandService subscriptionCommandService;
  @Mock
  private SubscriptionQueryService subscriptionQueryService;

  @InjectMocks
  private SubscriptionContextFacade subscriptionContextFacade;

  @Test
  @DisplayName("Given the command service returns a subscription, when creating, then it returns the id")
  void handle_ShouldReturnSubscriptionId_WhenCreating() {
    // Arrange
    Subscription subscription = mock(Subscription.class);
    when(subscription.getId()).thenReturn(5L);
    when(subscriptionCommandService.handle(any(CreateSubscriptionCommand.class)))
        .thenReturn(subscription);

    // Act
    Long result = subscriptionContextFacade.createSubscription(2L, 1L);

    // Assert
    assertEquals(5L, result);
  }

  @Test
  @DisplayName("Given the user cannot make a request, when checking, then it returns false")
  void handle_ShouldReturnFalse_WhenUserCannotMakeRequest() {
    // Arrange
    when(subscriptionQueryService.canUserMakeRequest(2L)).thenReturn(false);

    // Act
    boolean result = subscriptionContextFacade.canUserMakeRequest(2L);

    // Assert
    assertFalse(result);
  }

  @Test
  @DisplayName("Given a premium active subscription, when checking premium status, then it returns true")
  void handle_ShouldReturnTrue_WhenUserIsPremium() {
    // Arrange
    Subscription subscription = mock(Subscription.class);
    when(subscription.isPremium()).thenReturn(true);
    when(subscriptionQueryService.handle(any(GetActiveSubscriptionByUserIdQuery.class)))
        .thenReturn(Optional.of(subscription));

    // Act
    boolean result = subscriptionContextFacade.isPremiumUser(2L);

    // Assert
    assertTrue(result);
  }
}
