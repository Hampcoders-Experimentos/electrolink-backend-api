package com.hampcoders.electrolink.monitoring.application.internal.eventhandlers;

import com.hampcoders.electrolink.monitoring.domain.model.events.ServiceCompletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class MutualEvaluationTriggerHandlerTest {

    @InjectMocks
    private MutualEvaluationTriggerHandler handler;

    @Test
    @DisplayName("onServiceCompleted should log information and execute without exceptions (AAA)")
    void onServiceCompleted_ShouldExecuteSuccessfully() {
        // Arrange
        ServiceCompletedEvent event = new ServiceCompletedEvent(10L, 20L, 30L);

        // Act & Assert
        // Dado que el manejador solo hace un log de información sin llamadas externas,
        // simplemente verificamos que la ejecución no lance ninguna excepción.
        assertDoesNotThrow(() -> handler.onServiceCompleted(event));
    }
}
