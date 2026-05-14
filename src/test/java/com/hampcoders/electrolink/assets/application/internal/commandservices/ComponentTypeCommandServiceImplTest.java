package com.hampcoders.electrolink.assets.application.internal.commandservices;

import com.hampcoders.electrolink.assets.domain.model.aggregates.ComponentType;
import com.hampcoders.electrolink.assets.domain.model.commands.CreateComponentTypeCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.DeleteComponentTypeCommand;
import com.hampcoders.electrolink.assets.domain.model.commands.UpdateComponentTypeCommand;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.ComponentTypeId;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.ComponentRepository;
import com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories.ComponentTypeRepository;
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
class ComponentTypeCommandServiceImplTest {

    @Mock
    private ComponentTypeRepository componentTypeRepository;

    @Mock
    private ComponentRepository componentRepository;

    @InjectMocks
    private ComponentTypeCommandServiceImpl service;

    @Test
    @DisplayName("handle(CreateCommand) should create and return ComponentTypeId when name is unique (AAA)")
    void handleCreateCommand_ShouldCreate_WhenNameIsUnique() {
        // ARRANGE
        Long MOCK_TYPE_ID = 5L;
        CreateComponentTypeCommand command = new CreateComponentTypeCommand("UniqueType", "Desc");

        ComponentType savedTypeMock = mock(ComponentType.class);
        when(savedTypeMock.getId()).thenReturn(MOCK_TYPE_ID);

        when(componentTypeRepository.existsByName(command.name())).thenReturn(false);
        when(componentTypeRepository.save(any(ComponentType.class))).thenReturn(savedTypeMock);

        // ACT
        ComponentTypeId resultId = service.handle(command);

        // ASSERT
        assertNotNull(resultId);
        assertEquals(MOCK_TYPE_ID, resultId.id());
        verify(componentTypeRepository, times(1)).existsByName(command.name());
        verify(componentTypeRepository, times(1)).save(any(ComponentType.class));
        verifyNoInteractions(componentRepository);
    }

    @Test
    @DisplayName("handle(CreateCommand) should throw IllegalStateException when name exists (AAA)")
    void handleCreateCommand_ShouldThrowException_WhenNameExists() {
        // ARRANGE
        CreateComponentTypeCommand command = new CreateComponentTypeCommand("DuplicateType", "Desc");
        when(componentTypeRepository.existsByName(command.name())).thenReturn(true);

        // ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> {
            service.handle(command);
        }, "Debe lanzar IllegalStateException si el nombre del tipo ya existe.");

        // ASSERT
        verify(componentTypeRepository, times(1)).existsByName(command.name());
        verify(componentTypeRepository, never()).save(any());
        verifyNoMoreInteractions(componentTypeRepository, componentRepository);
    }

    @Test
    @DisplayName("handle(DeleteCommand) should return true when not in use and deleted (AAA)")
    void handleDeleteCommand_ShouldReturnTrue_WhenTypeExistsAndIsNotInUse() {
        // ARRANGE
        Long typeId = 3L;
        DeleteComponentTypeCommand command = new DeleteComponentTypeCommand(typeId);

        // Creamos la instancia exacta que el servicio construirá y usará
        ComponentTypeId expectedTypeIdVO = new ComponentTypeId(typeId);

        when(componentTypeRepository.existsById(typeId)).thenReturn(true);

        // Usamos la instancia exacta del Value Object en la simulación
        when(componentRepository.existsByComponentTypeId(expectedTypeIdVO)).thenReturn(false);

        // ACT
        Boolean result = service.handle(command);

        // ASSERT
        assertTrue(result);
        verify(componentTypeRepository, times(1)).existsById(typeId);
        verify(componentRepository, times(1)).existsByComponentTypeId(expectedTypeIdVO);
        verify(componentTypeRepository, times(1)).deleteById(typeId);
        verifyNoMoreInteractions(componentTypeRepository, componentRepository);
    }

    @Test
    @DisplayName("handle(DeleteCommand) should throw IllegalStateException when type is in use (AAA)")
    void handleDeleteCommand_ShouldThrowException_WhenTypeExistsAndIsInUse() {
        // ARRANGE
        Long typeId = 4L;
        DeleteComponentTypeCommand command = new DeleteComponentTypeCommand(typeId);

        // Creamos la instancia exacta que el servicio construirá y usará
        ComponentTypeId expectedTypeIdVO = new ComponentTypeId(typeId);

        when(componentTypeRepository.existsById(typeId)).thenReturn(true);

        //Usamos la instancia exacta del Value Object en la simulación
        when(componentRepository.existsByComponentTypeId(expectedTypeIdVO)).thenReturn(true);

        // ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> {
            service.handle(command);
        }, "Debe lanzar IllegalStateException si el tipo está en uso.");

        // ASSERT (Verificación)
        verify(componentTypeRepository, times(1)).existsById(typeId);
        // Verificamos la llamada con el Value Object esperado
        verify(componentRepository, times(1)).existsByComponentTypeId(expectedTypeIdVO);
        verify(componentTypeRepository, never()).deleteById(any());
        verifyNoMoreInteractions(componentTypeRepository, componentRepository);
    }

    @Test
    @DisplayName("handle(DeleteCommand) should return false when type does not exist (AAA)")
    void handleDeleteCommand_ShouldReturnFalse_WhenTypeDoesNotExist() {
        // ARRANGE
        Long typeId = 99L;
        DeleteComponentTypeCommand command = new DeleteComponentTypeCommand(typeId);
        when(componentTypeRepository.existsById(typeId)).thenReturn(false);

        // ACT
        Boolean result = service.handle(command);

        // ASSERT
        assertFalse(result);
        verify(componentTypeRepository, times(1)).existsById(typeId);
        verify(componentRepository, never()).existsByComponentTypeId(any());
        verify(componentTypeRepository, never()).deleteById(any());
        verifyNoMoreInteractions(componentTypeRepository);
    }

    @Test
    @DisplayName("handle(UpdateCommand) should update component type and return it when found (AAA)")
    void handleUpdateCommand_ShouldUpdateComponentType_WhenFound() {
        // ARRANGE
        Long typeId = 1L;
        String newName = "Updated Name";
        String newDesc = "Updated Desc";
        UpdateComponentTypeCommand command = new UpdateComponentTypeCommand(typeId, newName, newDesc);

        ComponentType mockType = mock(ComponentType.class);
        when(componentTypeRepository.findById(typeId)).thenReturn(Optional.of(mockType));
        when(componentTypeRepository.save(mockType)).thenReturn(mockType);

        // ACT
        Optional<ComponentType> result = service.handle(command);

        // ASSERT
        assertTrue(result.isPresent());
        verify(componentTypeRepository, times(1)).findById(typeId);
        verify(mockType, times(1)).updateName(command);
        verify(componentTypeRepository, times(1)).save(mockType);
        verifyNoMoreInteractions(componentTypeRepository, mockType);
    }
}