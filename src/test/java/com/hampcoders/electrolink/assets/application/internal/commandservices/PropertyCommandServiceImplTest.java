package com.hampcoders.electrolink.assets.application.internal.commandservices;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Property;
import com.hampcoders.electrolink.assets.domain.model.commands.CreatePropertyCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.DeletePropertyCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.UpdatePropertyCommand;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.PropertyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyCommandServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyCommandServiceImpl service;

    @Test
    @DisplayName("handle(CreateCommand) should create property and save it (AAA)")
    void handleCreateCommand_ShouldCreateProperty() {
        // ARRANGE
        CreatePropertyCommand command = new CreatePropertyCommand(null, null, null, null);

        // ACT
        UUID resultId = service.handle(command);

        // ASSERT
        // Dado que en el servicio actual:
        // var property = new Property(command);
        // propertyRepository.save(property);
        // return property.getId();
        // El UUID devuelto será null ya que no hay una base de datos real generando el ID en el test unitario.
        assertNull(resultId);
        verify(propertyRepository, times(1)).save(any(Property.class));
        verifyNoMoreInteractions(propertyRepository);
    }

    @Test
    @DisplayName("handle(UpdateCommand) should update property when found (AAA)")
    void handleUpdateCommand_ShouldUpdateProperty_WhenFound() {
        // ARRANGE
        UUID propertyId = UUID.randomUUID();
        UpdatePropertyCommand command = new UpdatePropertyCommand(propertyId, null, null, null);

        Property mockProperty = mock(Property.class);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(mockProperty));
        when(propertyRepository.save(mockProperty)).thenReturn(mockProperty);

        // ACT
        Optional<Property> result = service.handle(command);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(mockProperty, result.get());
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(mockProperty, times(1)).update(command);
        verify(propertyRepository, times(1)).save(mockProperty);
        verifyNoMoreInteractions(propertyRepository, mockProperty);
    }

    @Test
    @DisplayName("handle(UpdateCommand) should return empty Optional when property not found (AAA)")
    void handleUpdateCommand_ShouldReturnEmpty_WhenNotFound() {
        // ARRANGE
        UUID propertyId = UUID.randomUUID();
        UpdatePropertyCommand command = new UpdatePropertyCommand(propertyId, null, null, null);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // ACT
        Optional<Property> result = service.handle(command);

        // ASSERT
        assertTrue(result.isEmpty());
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(propertyRepository, never()).save(any());
        verifyNoMoreInteractions(propertyRepository);
    }

    @Test
    @DisplayName("handle(DeleteCommand) should return true when property found and updated (AAA)")
    void handleDeleteCommand_ShouldReturnTrue_WhenFound() {
        // ARRANGE
        UUID propertyId = UUID.randomUUID();
        DeletePropertyCommand command = new DeletePropertyCommand(propertyId);
        Property mockProperty = mock(Property.class);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(mockProperty));
        when(propertyRepository.save(mockProperty)).thenReturn(mockProperty);

        // ACT
        Boolean result = service.handle(command);

        // ASSERT
        assertTrue(result);
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(propertyRepository, times(1)).save(mockProperty);
        verifyNoMoreInteractions(propertyRepository, mockProperty);
    }

    @Test
    @DisplayName("handle(DeleteCommand) should return false when property not found (AAA)")
    void handleDeleteCommand_ShouldReturnFalse_WhenNotFound() {
        // ARRANGE
        UUID propertyId = UUID.randomUUID();
        DeletePropertyCommand command = new DeletePropertyCommand(propertyId);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // ACT
        Boolean result = service.handle(command);

        // ASSERT
        assertFalse(result);
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(propertyRepository, never()).save(any());
        verifyNoMoreInteractions(propertyRepository);
    }
}
