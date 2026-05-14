# Unit Tests for Monitoring Bounded Context

Este directorio contiene las pruebas unitarias para el subdominio `monitoring` (Monitoreo de Operaciones y Calificaciones). Las pruebas aseguran la correcta funcionalidad de la capa de aplicación y siguen estrictamente el patrón **Triple A (Arrange, Act, Assert)**.

## 1. Command Services (`application/internal/commandservices`)

Se han verificado las pruebas unitarias para los siguientes servicios de comando, los cuales garantizan la persistencia y actualización de los datos:
*   **`RatingCommandServiceImplTest`**: Pruebas para la creación y actualización de calificaciones (ratings).
*   **`ReportCommandServiceImplTest`**: Pruebas para la generación y gestión de estado de los reportes.
*   **`ReportPhotoCommandServiceImplTest`**: Pruebas para adjuntar y eliminar fotos de evidencia en los reportes.
*   **`ServiceOperationCommandServiceImplTest`**: Pruebas para iniciar, actualizar y finalizar las operaciones de servicio técnico.

## 2. Query Services (`application/internal/queryservices`)

Se han verificado las pruebas unitarias para los siguientes servicios de consulta, los cuales aseguran la correcta recuperación de datos:
*   **`RatingQueryServiceImplTest`**: Pruebas para recuperar calificaciones por ID o relaciones asociadas.
*   **`ReportQueryServiceImplTest`**: Pruebas para recuperar reportes por ID, estado o contexto.
*   **`ServiceOperationQueryServiceImplTest`**: Pruebas para consultar operaciones de servicio activas o relacionadas a usuarios.

## 3. Event Handlers (`application/internal/eventhandlers`)

*(Pruebas añadidas recientemente para garantizar el 100% de cobertura en la capa de aplicación).*

Se han implementado pruebas completas para los manejadores de eventos (handlers) que reaccionan a eventos de dominio (como `ServiceCompletedEvent`).

### `MutualEvaluationTriggerHandlerTest`
*   **`onServiceCompleted_ShouldExecuteSuccessfully`**: Verifica que al finalizar un servicio, se dispara correctamente el proceso (log) para habilitar la evaluación mutua entre el cliente y el técnico, sin generar excepciones.

### `StockDeductionOnServiceCompletedHandlerTest`
*   **`onServiceCompleted_ShouldDeductStock_WhenValid`**: Verifica que, al completar un servicio, el sistema averigua correctamente los componentes usados consultando al SDP Context, y descuenta esa cantidad del inventario del técnico usando el Inventory Context.
*   **`onServiceCompleted_ShouldSkip_WhenServiceIdNotFound`**: Comprueba que si la solicitud de servicio original no es encontrada por el facade, el manejador se detiene de forma segura sin realizar cambios en el inventario.
*   **`onServiceCompleted_ShouldSetToZero_WhenInsufficientStock`**: Verifica el caso límite en donde un técnico registra usar una cantidad mayor a la que tiene en inventario. Asegura que el stock disponible nunca quede en un valor negativo, sino que se actualice a `0`.
