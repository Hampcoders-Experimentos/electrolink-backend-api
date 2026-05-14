# Unit Tests for Subscription Bounded Context

Este directorio contiene las pruebas unitarias para el subdominio `subscription` (Gestión de Suscripciones y Planes). Al igual que los demás módulos del sistema, estas pruebas garantizan la estabilidad de la capa de aplicación implementando el patrón **Triple A (Arrange, Act, Assert)**.

## 1. Command Services (`application/internal/commandservices`)

### `PlanCommandServiceImplTest`
Pruebas para garantizar que la creación de planes (Basic, Premium, etc.) asigne correctamente los precios y nombres, y se persista en la base de datos sin duplicados.
*   **`handleCreateCommand_ShouldCreate`**: Verifica que el comando de creación de plan devuelva un resultado no nulo y ejecute el guardado en el repositorio correctamente.

### `SubscriptionCommandServiceImplTest`
Pruebas sobre la gestión de suscripciones de los usuarios, incluyendo verificaciones de renovaciones y creaciones iniciales.
*   **`handleCreateCommand_ShouldCreate_WhenNoExisting`**: Verifica que si el usuario no tiene suscripción previa, se le asigne y cree una nueva suscripción correctamente.
*   **`handleCreateCommand_ShouldUpgrade_WhenExisting`**: Comprueba que si el usuario ya cuenta con una suscripción, en lugar de crear una duplicada, se actualice (upgrade) la existente.
*   **`handleCreateCommand_ShouldThrow_WhenPlanNotFound`**: Verifica que se lance un error de argumento si se intenta suscribir a un usuario a un ID de plan que no existe.
*   **`handleUpgradeCommand_ShouldUpgrade`**: Comprueba el flujo directo del comando de actualización, asegurando que el estado anterior de la suscripción cambie al nuevo plan.
*   **`handleCancelCommand_ShouldCancel`**: Verifica que al invocar la cancelación, la suscripción del usuario pase a estado cancelado y se persista.
*   **`handleRecordRequestCommand_ShouldRecord`**: Comprueba que cuando un usuario hace una solicitud de servicio, el contador en su suscripción aumente y se guarde el registro.

## 2. Query Services (`application/internal/queryservices`)

### `PlanQueryServiceImplTest`
Verifica la funcionalidad de búsqueda de un plan mediante su ID o su tipo.
*   **`handleGetAllPlans_ShouldReturnAll`**: Verifica que la consulta devuelva la lista completa de planes del sistema.
*   **`handleGetPlanById_ShouldReturn_WhenFound`**: Comprueba que se retorne el plan cuando se proporciona un ID válido.
*   **`handleGetPlanById_ShouldReturnEmpty_WhenNotFound`**: Verifica que retorne un `Optional` vacío si el ID no existe.
*   **`findByType_ShouldReturn_WhenFound`**: Comprueba que se puede obtener un plan específico mandando el tipo (ej. `PREMIUM`).
*   **`findByType_ShouldReturnEmpty_WhenNotFound`**: Verifica que devuelva un valor vacío si ese tipo de plan no ha sido inicializado.

### `SubscriptionQueryServiceImplTest`
Pruebas enfocadas en las consultas de suscripciones activas y capacidades de los usuarios.
*   **`handleGetSubscriptionByUserId_ShouldReturn_WhenFound`**: Verifica la obtención de la suscripción sin importar su estado usando el ID de usuario.
*   **`handleGetSubscriptionByUserId_ShouldReturnEmpty_WhenNotFound`**: Comprueba el manejo de respuestas vacías si el usuario no tiene suscripción.
*   **`handleGetActiveSubscriptionByUserId_ShouldReturn_WhenFound`**: Verifica que devuelva únicamente la suscripción si está en estado **ACTIVO**.
*   **`handleGetActiveSubscriptionByUserId_ShouldReturnEmpty_WhenNotFound`**: Comprueba que devuelva vacío si la suscripción expiró o fue cancelada.
*   **`canUserMakeRequest_ShouldReturnTrue_WhenCanMakeRequest`**: Comprueba la regla de negocio que autoriza al usuario a realizar solicitudes.
*   **`canUserMakeRequest_ShouldReturnFalse_WhenCannotMakeRequest`**: Verifica que se bloquee el permiso si la suscripción no lo permite.
*   **`canUserMakeRequest_ShouldReturnFalse_WhenNoSubscription`**: Verifica que devuelva `false` automáticamente si el usuario ni siquiera tiene una suscripción.

## 3. Event Handlers (`application/internal/eventhandlers`)

### `PlanSeedOnStartupTest`
Prueba unitaria para el inicializador de base de datos de los planes por defecto.
*   **`onApplicationReady_ShouldSeedBothPlans`**: Verifica que si la base de datos está vacía al iniciar el sistema, se guarden automáticamente los planes `BASIC` y `PREMIUM`.
*   **`onApplicationReady_ShouldNotSeed_WhenPlansExist`**: Comprueba que si los planes ya existen, el manejador no realice operaciones de guardado (evitando duplicidad).

### `SubscriptionEventPublisherTest`
Prueba unitaria para los eventos de dominio de la suscripción.
*   **`onSubscriptionActivated_ShouldExecuteSuccessfully`**: Verifica que cuando ocurre el evento de dominio `SubscriptionActivatedEvent` (alguien adquiere una suscripción), el sistema proceda a registrar (log) el suceso sin generar excepciones que rompan la aplicación.
