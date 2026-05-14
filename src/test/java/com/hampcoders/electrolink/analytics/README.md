# Unit Tests for Analytics Bounded Context

Este directorio contiene las pruebas unitarias para el subdominio `analytics` (Módulo de Analíticas y Reportes). Este contexto está enfocado puramente en extracción y cálculo de métricas a partir de la información de otros dominios (SDP, Monitoring, Assets).

Al igual que en todo el proyecto, se sigue rigurosamente el patrón **Triple A (Arrange, Act, Assert)** para garantizar que la lógica de cálculo (promedios, sumatorias, filtrado por fechas) sea infalible.

## 1. Query Services (`application/internal/queryservices`)

### `AnalyticsQueryServiceImplTest`
Este es el servicio principal del contexto. Las pruebas verifican que las tres analíticas principales generen las matemáticas, agrupaciones y recuentos de manera correcta.

*   **`handle_GetHomeOwnerConsumption_ShouldReturnAggregatedData`**: 
    *   Verifica que la consulta `GetHomeOwnerConsumptionQuery` extraiga todas las solicitudes (*Requests*) de un propietario.
    *   Comprueba que se agrupen correctamente por Año y Mes.
    *   Valida la suma total de energía consumida y la suma total de pagos realizados.
    *   Valida el manejo seguro cuando un *Request* no tiene factura (bill).

*   **`handle_GetTechnicianPerformance_ShouldReturnMetrics`**: 
    *   Verifica que la consulta `GetTechnicianPerformanceQuery` obtenga el rendimiento del técnico.
    *   Comprueba que el recuento separe correctamente servicios `COMPLETED` de los `PENDING`.
    *   Valida el cálculo del promedio de horas trabajadas (tiempo entre *startedAt* y *completedAt*).
    *   Valida el cálculo del promedio de calificación (Rating) a partir de los puntajes.

*   **`handle_GetTechnicianRevenue_ShouldReturnAggregatedData`**:
    *   Verifica que la consulta `GetTechnicianRevenueQuery` extraiga las operaciones finalizadas.
    *   Comprueba que filtre las ganancias por una fecha de corte (*cutoff* de meses).
    *   Valida que para cada operación completada, busque el `Request` asociado y extraiga el pago (`amountPaid`) de la factura (`bill`).
    *   Valida la sumatoria del ingreso total, total de servicios realizados en el periodo y su ganancia promedio.
