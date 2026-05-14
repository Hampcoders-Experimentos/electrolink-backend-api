# Unit Tests for SDP Bounded Context

Este directorio contiene las pruebas unitarias para el subdominio `sdp` (Service Delivery Process), el cual es uno de los módulos más complejos del sistema ya que gestiona las solicitudes, cronogramas y el proceso central de entrega de los servicios. 

Las pruebas aseguran la correcta funcionalidad de todas las capas lógicas y siguen de forma estricta el patrón **Triple A (Arrange, Act, Assert)**.

## 1. Command Services (`application/internal/commandservices`)

Estos servicios han sido evaluados para asegurar que las acciones que cambian el estado del sistema operen correctamente:
*   **`RequestCommandServiceImplTest`**: Pruebas para la creación, actualización y gestión de las solicitudes de servicio realizadas por el cliente.
*   **`ScheduleCommandServiceImplTest`**: Pruebas para la creación y actualización de los horarios de disponibilidad del técnico.
*   **`ServiceCommandServiceImplTest`**: Pruebas para gestionar la entidad del servicio como tal.

## 2. Query Services (`application/internal/queryservices`)

Estos servicios de consulta han sido evaluados para confirmar que la información se devuelve según los requerimientos:
*   **`RequestQueryServiceImplTest`**: Verifica las búsquedas de solicitudes por distintos parámetros (por cliente, por estado, etc.).
*   **`ScheduleQueryServiceImplTest`**: Asegura que el cronograma y la disponibilidad del técnico se puedan consultar correctamente.
*   **`ServiceQueryServiceImplTest`**: Verifica las consultas de los catálogos y especificaciones de los servicios.

## 3. Outbound Services & Services (`application/internal/outboundservices` & `services`)

*   **`ExternalProfileServiceTest`**: Verifica que el sistema se comunique correctamente con el Bounded Context de *Profiles* a través de su Facade, para recuperar la información de los técnicos cuando sea requerido.
*   **`TechnicianMatchingServiceTest`**: Pruebas unitarias para el algoritmo de emparejamiento de técnicos (`findBestTechnicianForRequest`). Verifica:
    *   Que retorne `Optional.empty()` si no hay técnicos registrados.
    *   Que retorne un técnico si coincide su horario disponible con el de la solicitud **y además** tiene inventario.
    *   Que ignore a los técnicos que no tienen el stock necesario.
    *   Que si una solicitud está marcada como **Prioritaria** (`isPriority() == true`), se asigne un técnico disponible por horario, incluso si no se ha validado su stock (mecanismo de *fallback* o plan de contingencia implementado en la regla de negocio).
