package com.hampcoders.electrolink.subscription.application.internal.eventhandlers;

import com.hampcoders.electrolink.subscription.domain.model.events.SubscriptionActivatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionEventPublisherTest {

    @InjectMocks
    private SubscriptionEventPublisher publisher;

    @Test
    @DisplayName("onSubscriptionActivated should execute without exceptions (AAA)")
    void onSubscriptionActivated_ShouldExecuteSuccessfully() {
        // Arrange
        SubscriptionActivatedEvent event = mock(SubscriptionActivatedEvent.class);
        when(event.userId()).thenReturn(1L);
        when(event.planId()).thenReturn(2L);
        when(event.planName()).thenReturn(com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType.PREMIUM);

        // Act & Assert
        // Como este event handler solo imprime un log y no tiene dependencias inyectadas,
        // simplemente verificamos que la ejecución se complete correctamente sin errores.
        assertDoesNotThrow(() -> publisher.onSubscriptionActivated(event));
    }
}
