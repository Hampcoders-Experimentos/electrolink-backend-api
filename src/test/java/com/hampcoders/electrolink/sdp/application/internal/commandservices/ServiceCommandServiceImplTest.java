package com.hampcoders.electrolink.sdp.application.internal.commandservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.DeleteServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Policy;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Restriction;
import com.hampcoders.electrolink.sdp.infrastructure.persistence.jpa.repositories.ServiceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceCommandServiceImplTest {

  @Mock
  private ServiceRepository serviceRepository;

  @InjectMocks
  private ServiceCommandServiceImpl serviceCommandService;

  @Test
  @DisplayName("Given a valid command, when handling CreateServiceCommand, then it returns the saved id")
  void handle_ShouldReturnServiceId_WhenCreated() {
    // Arrange
    CreateServiceCommand command = new CreateServiceCommand(
        "Install", "desc", 100.0, "2h", "ELECTRICAL", true, "admin",
        new Policy("cancel", "terms"),
        new Restriction(List.of(), List.of(), false),
        List.of(), List.of());
    ServiceEntity saved = mock(ServiceEntity.class);
    when(saved.getId()).thenReturn(3L);
    when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(saved);

    // Act
    Long result = serviceCommandService.handle(command);

    // Assert
    assertEquals(3L, result);
  }

  @Test
  @DisplayName("Given a missing service, when handling UpdateServiceCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenUpdatingMissingService() {
    // Arrange
    UpdateServiceCommand command = new UpdateServiceCommand(
        10L, null, null, null, null, null, false, null, null, null, null, null);
    when(serviceRepository.findById(10L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> serviceCommandService.handle(command));
  }

  @Test
  @DisplayName("Given a missing service, when handling DeleteServiceCommand, then it throws IllegalArgument")
  void handle_ShouldThrow_WhenDeletingMissingService() {
    // Arrange
    DeleteServiceCommand command = new DeleteServiceCommand(7L);
    when(serviceRepository.existsById(7L)).thenReturn(false);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> serviceCommandService.handle(command));
  }
}
