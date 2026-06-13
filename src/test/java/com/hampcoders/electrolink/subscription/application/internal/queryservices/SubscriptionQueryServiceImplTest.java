package com.hampcoders.electrolink.subscription.application.internal.queryservices;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetActiveSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.SubscriptionStatus;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionQueryServiceImplTest {

  @Mock
  private SubscriptionRepository subscriptionRepository;

  @InjectMocks
  private SubscriptionQueryServiceImpl subscriptionQueryService;

  @Test
  @DisplayName("Given an existing subscription, when handling GetSubscriptionByUserIdQuery, then it returns it")
  void handle_ShouldReturnSubscription_WhenUserHasSubscription() {
    // Arrange
    Subscription subscription = mock(Subscription.class);
    when(subscriptionRepository.findByUserId(2L)).thenReturn(Optional.of(subscription));

    // Act
    Optional<Subscription> result =
        subscriptionQueryService.handle(new GetSubscriptionByUserIdQuery(2L));

    // Assert
    assertTrue(result.isPresent());
    assertSame(subscription, result.get());
  }

  @Test
  @DisplayName("Given no active subscription, when handling GetActiveSubscriptionByUserIdQuery, then it returns empty")
  void handle_ShouldReturnEmpty_WhenNoActiveSubscription() {
    // Arrange
    when(subscriptionRepository.findByUserIdAndStatus(2L, SubscriptionStatus.ACTIVE))
        .thenReturn(Optional.empty());

    // Act
    Optional<Subscription> result =
        subscriptionQueryService.handle(new GetActiveSubscriptionByUserIdQuery(2L));

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Given a subscription that allows requests, when checking canUserMakeRequest, then it returns true")
  void handle_ShouldReturnTrue_WhenSubscriptionAllowsRequest() {
    // Arrange
    Subscription subscription = mock(Subscription.class);
    when(subscription.canMakeRequest()).thenReturn(true);
    when(subscriptionRepository.findByUserId(2L)).thenReturn(Optional.of(subscription));

    // Act
    boolean result = subscriptionQueryService.canUserMakeRequest(2L);

    // Assert
    assertTrue(result);
  }
}
