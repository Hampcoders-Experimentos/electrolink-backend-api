# Unit Tests for SDP Bounded Context

Este directorio contiene las pruebas unitarias para el subdominio `sdp` (Service Delivery Process), el cual es uno de los módulos más complejos del sistema ya que gestiona las solicitudes, cronogramas y el proceso central de entrega de los servicios. 

Las pruebas aseguran la correcta funcionalidad de todas las capas lógicas y siguen de forma estricta el patrón **Triple A (Arrange, Act, Assert)**.

## 1. Command Services (`application/internal/commandservices`)

### `RequestCommandServiceImplTest`
Pruebas para la creación, actualización y gestión de las solicitudes de servicio realizadas por el cliente.
*   **`handle_CreateRequest_ShouldSaveAndReturnRequest`**: Verifica que al enviar un comando de creación válido, la solicitud se guarde en base de datos y se registre la métrica mediante el facade de suscripciones.
*   **`handle_CreateRequest_WithoutPhotos_ShouldCreateSuccessfully`**: Comprueba que el sistema permita crear solicitudes sin fotos adjuntas (valores opcionales).
*   **`handle_CreateRequest_WhenLimitReached_ShouldThrowException`**: Verifica que si el usuario excedió su límite de solicitudes de su plan, el sistema bloquee la acción.
*   **`handle_UpdateRequest_WhenRequestExists_ShouldUpdateSuccessfully`**: Comprueba que una solicitud existente actualice sus campos de forma correcta y persista los cambios.
*   **`handle_UpdateRequest_WhenRequestDoesNotExist_ShouldThrowException`**: Verifica el manejo de errores al intentar actualizar un ID de solicitud inexistente.
*   **`handle_DeleteRequest_WhenRequestExists_ShouldDeleteSuccessfully`**: Comprueba que la acción de eliminación llame al repositorio correctamente.
*   **`handle_DeleteRequest_WhenRequestDoesNotExist_ShouldThrowException`**: Verifica el manejo de errores al intentar eliminar un ID inexistente.

### `ScheduleCommandServiceImplTest`
Pruebas para la creación y actualización de los horarios de disponibilidad del técnico.
*   **`handle_CreateSchedule_ShouldSaveAndReturnId`**: Verifica que al crear un horario se guarde con éxito y se devuelva el ID autogenerado.
*   **`handle_CreateSchedule_WithValidData_ShouldCreateCorrectSchedule`**: Comprueba el mapeo de datos entre el comando y la entidad agregada.
*   **`handle_UpdateSchedule_WhenScheduleExists_ShouldUpdateSuccessfully`**: Verifica que los datos del horario de un técnico se actualicen con base en el comando.
*   **`handle_UpdateSchedule_WhenScheduleDoesNotExist_ShouldThrowException`**: Comprueba el manejo de errores al modificar horarios inexistentes.
*   **`handle_DeleteSchedule_WhenScheduleExists_ShouldDeleteSuccessfully`**: Verifica la funcionalidad de borrar una disponibilidad horaria.
*   **`handle_DeleteSchedule_WhenScheduleDoesNotExist_ShouldThrowException`**: Valida que no se rompa el flujo al eliminar registros vacíos.

### `ServiceCommandServiceImplTest`
Pruebas para gestionar la entidad del servicio base.
*   **`testHandleCreateServiceCommand_Success`**: Verifica la inserción exitosa de la parametrización de un nuevo servicio.
*   **`testHandleUpdateServiceCommand_Success`**: Comprueba la actualización de los datos del servicio.
*   **`testHandleUpdateServiceCommand_ServiceNotFound`**: Verifica que falle al actualizar servicios inválidos.
*   **`testHandleDeleteServiceCommand_Success`**: Comprueba la correcta eliminación.
*   **`testHandleDeleteServiceCommand_ServiceNotFound`**: Valida protección ante eliminaciones de entidades no existentes.

## 2. Query Services (`application/internal/queryservices`)

### `RequestQueryServiceImplTest`
Verifica las búsquedas de solicitudes por distintos parámetros.
*   **`handle_FindById_WhenRequestExists_ShouldReturnOptionalWithRequest`**: Comprueba búsqueda directa por ID.
*   **`handle_FindById_WhenRequestDoesNotExist_ShouldReturnEmptyOptional`**: Verifica casos nulos de búsqueda.
*   **`handle_FindByClientId_WhenMultipleRequestsExist_ShouldReturnList`**: Verifica que devuelva el arreglo correcto cuando el usuario tiene varias solicitudes activas.
*   **`handle_FindByClientId_WhenNoRequestsExist_ShouldReturnEmptyList`**: Comprueba el manejo de colecciones vacías.
*   **`handle_FindByClientId_WhenSingleRequestExists_ShouldReturnListWithOne`**: Verifica el funcionamiento cuando solo hay un resultado.

### `ScheduleQueryServiceImplTest`
Asegura que el cronograma y la disponibilidad del técnico se puedan consultar correctamente.
*   **`handle_FindById_WhenScheduleExists_ShouldReturnOptionalWithSchedule`**: Verifica búsqueda por ID de horario.
*   **`handle_FindById_WhenScheduleDoesNotExist_ShouldReturnEmptyOptional`**: Verifica manejo de opcionales vacíos.
*   **`handle_FindByTechnicianId_WhenMultipleSchedulesExist_ShouldReturnList`**: Comprueba que liste todos los días/horas en los que el técnico trabaja.
*   **`handle_FindByTechnicianId_WhenNoSchedulesExist_ShouldReturnEmptyList`**: Verifica listas vacías para técnicos sin horarios.
*   **`handle_FindByTechnicianId_WhenSingleScheduleExists_ShouldReturnListWithOne`**: Comprueba el retorno unitario en lista.

### `ServiceQueryServiceImplTest`
Verifica las consultas de los catálogos y especificaciones de los servicios.
*   **`handle_FindById_WhenServiceExists_ShouldReturnOptionalWithService`**: Búsqueda unitaria por ID de servicio.
*   **`handle_FindById_WhenServiceDoesNotExist_ShouldReturnEmptyOptional`**: Manejo de errores por ID falso.
*   **`handle_GetAll_WhenMultipleServicesExist_ShouldReturnList`**: Verifica la recuperación de todo el catálogo.
*   **`handle_GetAll_WhenNoServicesExist_ShouldReturnEmptyList`**: Comprueba que el sistema liste vacío si no hay data.
*   **`handle_GetAll_WhenSingleServiceExists_ShouldReturnListWithOne`**: Comprueba la recuperación si hay solo un elemento configurado.

## 3. Outbound Services & Services (`application/internal/outboundservices` & `services`)

### `ExternalProfileServiceTest`
*   **`fetchTechnicians_ShouldReturnList`**: Verifica que el sistema se comunique correctamente con el Bounded Context de *Profiles* a través de su Facade, mandando traer perfiles con el enumerado de *TECHNICIAN*.

### `TechnicianMatchingServiceTest`
Pruebas unitarias para el algoritmo de emparejamiento de técnicos (`findBestTechnicianForRequest`).
*   **`findBestTechnicianForRequest_ShouldReturnEmpty_WhenNoTechnicians`**: Que retorne `Optional.empty()` si no hay técnicos registrados en la plataforma.
*   **`findBestTechnicianForRequest_ShouldReturnTech_WhenMatchesAll`**: Que retorne un técnico si coincide su horario disponible con el de la solicitud **y además** tiene inventario.
*   **`findBestTechnicianForRequest_ShouldReturnEmpty_WhenNoStockAndNotPriority`**: Que ignore a los técnicos que no tienen el stock necesario.
*   **`findBestTechnicianForRequest_ShouldReturnTech_WhenNoStockButPriority`**: Que si una solicitud está marcada como **Prioritaria** (`isPriority() == true`), se asigne un técnico disponible por horario, incluso si no se ha validado su stock (mecanismo de *fallback* o plan de contingencia implementado en la regla de negocio).
