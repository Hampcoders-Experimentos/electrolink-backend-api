# Unit Tests for Profiles Bounded Context

Este directorio contiene las pruebas unitarias para el subdominio `profiles` (Gestión de Perfiles). Las pruebas aseguran la correcta funcionalidad de la capa de aplicación y siguen estrictamente el patrón **Triple A (Arrange, Act, Assert)**.

## 1. Command Services (`application/internal/commandservices`)

### `ProfileCommandServiceImplTest`
Pruebas para el servicio de comandos de perfiles (`ProfileCommandServiceImpl`). Este servicio maneja la persistencia y la validación de la información de los usuarios (ya sean `HomeOwner` o `Technician`).

*   **`handleCreateProfile_Success_HomeOwner`**: Verifica que se crea correctamente un perfil para un rol de HomeOwner, asignando su información y generando su ID respectivo.
*   **`handleCreateProfile_Success_Technician`**: Comprueba que al crear un perfil para un Technician, además de guardar su información, se llama al servicio externo (`ExternalAssetsService`) para inicializar su inventario.
*   **`handleCreateProfile_EmailExists_ShouldThrow`**: Verifica que se lance una excepción si se intenta registrar un perfil con un correo electrónico ya utilizado.
*   **`handleCreateProfile_SaveError_ShouldThrow`**: Comprueba que si la base de datos falla al guardar, el servicio lanza la excepción adecuada con el mensaje descriptivo del error.
*   **`handleUpdateProfile_Success`**: Verifica que al solicitar la actualización de un perfil, sus atributos se modifiquen correctamente y se guarde el cambio en el repositorio.
*   **`handleUpdateProfile_ProfileNotFound_ShouldThrow`**: Comprueba que lanza una excepción si se intenta actualizar un perfil que no existe en el sistema.
*   **`handleUpdateProfile_EmailUsedByOther_ShouldThrow`**: Verifica que al actualizar, si se intenta usar un correo que le pertenece a otro perfil distinto, se aborte el cambio y se lance una excepción.
*   **`handleUpdateProfile_SaveError_ShouldThrow`**: Comprueba que errores en tiempo de guardado (persistencia) generen excepciones correctamente durante la actualización.
*   **`handleDeleteProfile_Success`**: Verifica que se elimine correctamente un perfil existente del sistema.
*   **`handleDeleteProfile_ProfileNotFound_ShouldThrow`**: Comprueba que se lance una excepción si se intenta borrar un ID de perfil que no existe.
*   **`handleDeleteProfile_DeleteError_ShouldThrow`**: Verifica el manejo de errores si ocurre una falla inesperada a la hora de borrar en la base de datos.

## 2. Query Services (`application/internal/queryservices`)

### `ProfileQueryServiceImplTest`
Pruebas para el servicio de consultas de perfiles (`ProfileQueryServiceImpl`).
*   **`handleGetAllProfiles_ShouldReturnList`**: Verifica que la consulta devuelva la lista con todos los perfiles registrados en el sistema.
*   **`handleGetProfileById_ShouldReturnOptional`**: Comprueba que al buscar un perfil por su ID, este sea recuperado y envuelto en un `Optional`.
*   **`handleGetProfileByFullName_ShouldReturnOptional`**: Verifica la funcionalidad de búsqueda exacta por nombre y apellido (`firstName` y `lastName`).
*   **`handleGetProfileByEmail_ShouldReturnOptional`**: Comprueba que la búsqueda mediante correo electrónico devuelve correctamente el perfil asociado.
*   **`handleGetProfilesByRole_ShouldReturnList`**: Verifica que la consulta por rol específico (por ejemplo, buscar todos los `TECHNICIAN`) retorne la lista correcta filtrada.
*   **`handleGetProfileByAge_ShouldThrowException`**: Verifica explícitamente que la consulta por edad no esté soportada en el modelo actual y arroje `UnsupportedOperationException`.
