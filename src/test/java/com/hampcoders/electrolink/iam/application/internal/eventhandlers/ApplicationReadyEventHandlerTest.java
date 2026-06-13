package com.hampcoders.electrolink.iam.application.internal.eventhandlers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.iam.domain.model.commands.SeedRolesCommand;
import com.hampcoders.electrolink.iam.domain.services.RoleCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;

@ExtendWith(MockitoExtension.class)
class ApplicationReadyEventHandlerTest {

  @Mock
  private RoleCommandService roleCommandService;

  @Mock
  private ApplicationReadyEvent event;

  @Mock
  private ConfigurableApplicationContext applicationContext;

  @InjectMocks
  private ApplicationReadyEventHandler handler;

  @Test
  @DisplayName("Given the application is ready, when handling the event, then it delegates roles seeding")
  void handle_ShouldSeedRoles_WhenApplicationReady() {
    // Arrange
    when(event.getApplicationContext()).thenReturn(applicationContext);
    when(applicationContext.getId()).thenReturn("electrolink");

    // Act
    handler.on(event);

    // Assert
    verify(roleCommandService, times(1)).handle(any(SeedRolesCommand.class));
  }

  @Test
  @DisplayName("Given roles seeding fails, when handling the event, then it propagates the exception")
  void handle_ShouldPropagateException_WhenSeedingFails() {
    // Arrange
    when(event.getApplicationContext()).thenReturn(applicationContext);
    when(applicationContext.getId()).thenReturn("electrolink");
    doThrow(new RuntimeException("seeding error"))
        .when(roleCommandService).handle(any(SeedRolesCommand.class));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> handler.on(event));
  }

  @Test
  @DisplayName("Given a null application id, when handling the event, then it still delegates roles seeding")
  void handle_ShouldSeedRoles_WhenApplicationIdIsNull() {
    // Arrange
    when(event.getApplicationContext()).thenReturn(applicationContext);
    when(applicationContext.getId()).thenReturn(null);

    // Act
    handler.on(event);

    // Assert
    verify(roleCommandService, times(1)).handle(any(SeedRolesCommand.class));
  }
}
