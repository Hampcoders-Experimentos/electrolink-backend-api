package com.hampcoders.electrolink.subscription.application.internal.queryservices;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetActiveSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetSubscriptionByUserIdQuery;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.SubscriptionStatus;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionQueryServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @InjectMocks
    private SubscriptionQueryServiceImpl subscriptionQueryService;

    private final Long USER_ID = 1L;

    @Test
    @DisplayName("handle(GetSubscriptionByUserIdQuery) should return subscription when found (AAA)")
    void handleGetSubscriptionByUserId_ShouldReturn_WhenFound() {
        var subscription = mock(Subscription.class);
        var query = new GetSubscriptionByUserIdQuery(USER_ID);

        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.of(subscription));

        var result = subscriptionQueryService.handle(query);

        assertTrue(result.isPresent());
        assertEquals(subscription, result.get());
        verify(subscriptionRepository).findByUserId(USER_ID);
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    @DisplayName("handle(GetSubscriptionByUserIdQuery) should return empty when not found (AAA)")
    void handleGetSubscriptionByUserId_ShouldReturnEmpty_WhenNotFound() {
        var query = new GetSubscriptionByUserIdQuery(USER_ID);

        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        var result = subscriptionQueryService.handle(query);

        assertTrue(result.isEmpty());
        verify(subscriptionRepository).findByUserId(USER_ID);
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    @DisplayName("handle(GetActiveSubscriptionByUserIdQuery) should return active subscription when found (AAA)")
    void handleGetActiveSubscriptionByUserId_ShouldReturn_WhenFound() {
        var subscription = mock(Subscription.class);
        var query = new GetActiveSubscriptionByUserIdQuery(USER_ID);

        when(subscriptionRepository.findByUserIdAndStatus(USER_ID, SubscriptionStatus.ACTIVE))
            .thenReturn(Optional.of(subscription));

        var result = subscriptionQueryService.handle(query);

        assertTrue(result.isPresent());
        assertEquals(subscription, result.get());
        verify(subscriptionRepository).findByUserIdAndStatus(USER_ID, SubscriptionStatus.ACTIVE);
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    @DisplayName("handle(GetActiveSubscriptionByUserIdQuery) should return empty when not found (AAA)")
    void handleGetActiveSubscriptionByUserId_ShouldReturnEmpty_WhenNotFound() {
        var query = new GetActiveSubscriptionByUserIdQuery(USER_ID);

        when(subscriptionRepository.findByUserIdAndStatus(USER_ID, SubscriptionStatus.ACTIVE))
            .thenReturn(Optional.empty());

        var result = subscriptionQueryService.handle(query);

        assertTrue(result.isEmpty());
        verify(subscriptionRepository).findByUserIdAndStatus(USER_ID, SubscriptionStatus.ACTIVE);
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    @DisplayName("canUserMakeRequest should return true when subscription can make request (AAA)")
    void canUserMakeRequest_ShouldReturnTrue_WhenCanMakeRequest() {
        var subscription = mock(Subscription.class);

        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.of(subscription));
        when(subscription.canMakeRequest()).thenReturn(true);

        var result = subscriptionQueryService.canUserMakeRequest(USER_ID);

        assertTrue(result);
        verify(subscriptionRepository).findByUserId(USER_ID);
        verify(subscription).canMakeRequest();
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    @DisplayName("canUserMakeRequest should return false when subscription cannot make request (AAA)")
    void canUserMakeRequest_ShouldReturnFalse_WhenCannotMakeRequest() {
        var subscription = mock(Subscription.class);

        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.of(subscription));
        when(subscription.canMakeRequest()).thenReturn(false);

        var result = subscriptionQueryService.canUserMakeRequest(USER_ID);

        assertFalse(result);
        verify(subscriptionRepository).findByUserId(USER_ID);
        verify(subscription).canMakeRequest();
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    @DisplayName("canUserMakeRequest should return false when no subscription found (AAA)")
    void canUserMakeRequest_ShouldReturnFalse_WhenNoSubscription() {
        when(subscriptionRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        var result = subscriptionQueryService.canUserMakeRequest(USER_ID);

        assertFalse(result);
        verify(subscriptionRepository).findByUserId(USER_ID);
        verifyNoMoreInteractions(subscriptionRepository);
    }
}
