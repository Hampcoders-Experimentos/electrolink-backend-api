# Unit Tests for IAM Bounded Context

Este directorio contiene las pruebas unitarias para el subdominio `iam` (Identity and Access Management). Todas las pruebas han sido verificadas y siguen de forma estricta el patrón **Triple A (Arrange, Act, Assert)**.

## 1. Command Services (`application/internal/commandservices`)

### `RoleCommandServiceImplTest`
Pruebas para el servicio de comandos de roles (`RoleCommandServiceImpl`).
*   **`handle_ShouldSeed_AllMissingRoles`**: Verifica que el comando de inicialización (`SeedRolesCommand`) guarde en la base de datos todos los roles del sistema que aún no existan.
*   **`handle_ShouldSkip_WhenRoleExists`**: Comprueba que si algunos roles ya existen en la base de datos, el servicio los ignora y solo guarda los que hacen falta.

### `UserCommandServiceImplTest`
Pruebas para el servicio de comandos de usuarios (`UserCommandServiceImpl`).
*   **`handle_SignIn_ShouldReturnPair_WhenValid`**: Verifica que, al iniciar sesión con credenciales válidas, el sistema retorna el usuario junto con un token JWT generado.
*   **`handle_SignIn_ShouldThrow_WhenUserMissing`**: Comprueba que se lanza una excepción si se intenta iniciar sesión con un nombre de usuario que no existe.
*   **`handle_SignIn_ShouldThrow_WhenPasswordInvalid`**: Verifica que se lanza una excepción si la contraseña proporcionada es incorrecta.
*   **`handle_SignUp_ShouldCreate_WhenValid`**: Verifica que al registrarse con un nombre de usuario nuevo y roles válidos, se crea exitosamente el usuario en la base de datos.
*   **`handle_SignUp_ShouldThrow_WhenUsernameExists`**: Comprueba que se lanza una excepción si el nombre de usuario ya está registrado.
*   **`handle_SignUp_ShouldThrow_WhenRoleNotFound`**: Verifica que se lanza una excepción si alguno de los roles solicitados en el registro no existe en el sistema.

## 2. Query Services (`application/internal/queryservices`)

### `RoleQueryServiceImplTest`
Pruebas para el servicio de consultas de roles (`RoleQueryServiceImpl`).
*   **`handle_GetAll_ShouldReturnList`**: Verifica que la consulta `GetAllRolesQuery` devuelva correctamente todos los roles almacenados.
*   **`handle_GetAll_ShouldReturnEmptyList_WhenRepositoryIsEmpty`**: Comprueba que si no hay roles, la consulta devuelva una lista vacía.
*   **`handle_GetByName_ShouldReturnOptional`**: Verifica que al buscar un rol por su nombre (`GetRoleByNameQuery`), se devuelva correctamente encapsulado en un `Optional`.
*   **`handle_GetByName_ShouldReturnEmptyOptional_WhenNotFound`**: Comprueba que si el rol buscado no existe, se devuelve un `Optional` vacío.

### `UserQueryServiceImplTest`
Pruebas para el servicio de consultas de usuarios (`UserQueryServiceImpl`).
*   **`handle_GetAll_ShouldReturnList`**: Verifica que se devuelva la lista de todos los usuarios registrados.
*   **`handle_GetAll_ShouldReturnEmptyList`**: Comprueba que si la base de datos de usuarios está vacía, se retorna una lista vacía.
*   **`handle_GetById_ShouldReturnOptional`**: Verifica que al buscar un usuario por su ID (`GetUserByIdQuery`), se retorna el usuario correspondiente.
*   **`handle_GetById_ShouldReturnEmptyOptional`**: Comprueba que si el ID no existe, se retorna un `Optional` vacío.
*   **`handle_GetByUsername_ShouldReturnOptional`**: Verifica que la búsqueda por nombre de usuario (`GetUserByUsernameQuery`) funciona y retorna al usuario correcto.
*   **`handle_GetByUsername_ShouldReturnEmptyOptional`**: Comprueba que si el nombre de usuario no existe, devuelve un `Optional` vacío.

## 3. Event Handlers (`application/internal/eventhandlers`)

### `ApplicationReadyEventHandlerTest`
Pruebas para los manejadores de eventos al iniciar la aplicación.
*   **`on_ShouldDelegateSeeding`**: Verifica que, una vez que la aplicación ha iniciado (al recibir un `ApplicationReadyEvent`), se delega correctamente la orden de inicializar los roles (`SeedRolesCommand`) al servicio correspondiente.
