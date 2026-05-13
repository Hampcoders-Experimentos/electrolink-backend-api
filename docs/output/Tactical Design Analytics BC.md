# Electrolink Platform — Spring Boot | DDD | CQRS | Monolito Modular

---

## 📚 ÍNDICE

1. [Estructura del Proyecto](#estructura)
2. [Domain Layer](#domain)
3. [Application Layer](#application)
4. [Infrastructure Layer](#infrastructure)
5. [Interfaces Layer](#interfaces)
6. [Flujos CQRS por Caso de Uso](#flujos)
7. [Integración con otros BCs](#integracion)

---

<a name="estructura"></a>

## 1. Estructura del Proyecto

```
📁 src/main/java/com/hampcoders/electrolink/analytics/
│
├── 📁 application/
│   └── 📁 internal/
│       └── 📁 queryservices/
│           └── AnalyticsQueryServiceImpl.java
│
├── 📁 domain/
│   ├── 📁 model/
│   │   └── 📁 queries/
│   │       ├── GetHomeOwnerConsumptionQuery.java
│   │       ├── GetTechnicianPerformanceQuery.java
│   │       └── GetTechnicianRevenueQuery.java
│   └── 📁 services/
│       └── AnalyticsQueryService.java
│
└── 📁 interfaces/
    └── 📁 rest/
        ├── AnalyticsController.java
        ├── 📁 resources/
        │   ├── HomeOwnerConsumptionResource.java
        │   ├── TechnicianPerformanceResource.java
        │   └── TechnicianRevenueResource.java
        └── 📁 transform/
            └── AnalyticsResourceFromEntityAssembler.java
```

---

<a name="domain"></a>

## 2. Domain Layer

**Patrones aplicados (Domain):** Queries (CQRS - Read), Value Objects.

**Nota:** El BC Analytics es un BC de solo lectura. No tiene Aggregates, Commands, ni Entity definitions propias. Consulta datos de otros BCs (SDP, Monitoring, Assets) y proporciona visualizaciones agregadas.

---

### 2.1 Queries (CQRS - Read)

```java
public record GetHomeOwnerConsumptionQuery(Long ownerId, int months) {}
public record GetTechnicianPerformanceQuery(Long technicianId) {}
public record GetTechnicianRevenueQuery(Long technicianId, int months) {}
```

---

### 2.2 Domain Services

|Servicio|Responsabilidad|
|:--|:--|
|`AnalyticsQueryService`|Puerto de lectura para consultas analíticas. Retorna `List<>` de recursos.|

---

<a name="application"></a>

## 3. Application Layer

---

### 3.1 Query Services

**Ubicación:** `src/main/java/com/hampcoders/electrolink/analytics/application/internal/queryservices/AnalyticsQueryServiceImpl`

**Responsabilidad:** Orquestar las consultas analíticas consultando repositorios de otros BCs:

- **HomeOwner Consumption**: Consulta `RequestRepository` (SDP BC), filtra por cliente y período (meses), agrupa por mes/año, calcula energía consumida y monto pagado desde los `Bill` de cada solicitud.
- **Technician Performance**: Consulta `ServiceOperationRepository` (Monitoring BC) y `RatingRepository` (Monitoring BC). Calcula servicios completados, promedio de rating, tiempo promedio de finalización y servicios pendientes.
- **Technician Revenue**: Consulta `ServiceOperationRepository` (Monitoring BC) filtrando completados, agrupa por mes/año, consulta `RequestRepository` para obtener montos pagados y calcula ingresos totales y promedio por servicio.

**Repositorios inyectados:**
- `ServiceOperationRepository` (Monitoring BC)
- `RequestRepository` (SDP BC)
- `PropertyRepository` (Assets BC) — disponible para futura expansión
- `RatingRepository` (Monitoring BC)

---

<a name="infrastructure"></a>

## 4. Infrastructure Layer

**Nota:** El BC Analytics no tiene repositorios propios. Utiliza repositorios JPA de otros BCs (SDP, Monitoring, Assets) mediante inyección directa de dependencias.

**En una implementación completa, debería tener:**
- Tablas de datos agregados/denormalizados optimizados para lectura (proyecciones CQRS).
- Integración con servicios externos de visualización (ej. Grafana, dashboards custom).

---

<a name="interfaces"></a>

## 5. Interfaces Layer

---

### 5.1 REST Controller

**Ubicación:** `src/main/java/com/hampcoders/electrolink/analytics/interfaces/rest/AnalyticsController.java`

**Endpoints expuestos:**

|Método|Ruta|Auth|Descripción|
|:--|:--|:--|:--|
|`GET`|`/api/v1/analytics/homeowners/{ownerId}/consumption?months=12`|JWT|Consumo histórico del propietario (agrupado por mes)|
|`GET`|`/api/v1/analytics/technicians/{technicianId}/performance`|JWT|Desempeño del técnico|
|`GET`|`/api/v1/analytics/technicians/{technicianId}/revenue?months=6`|JWT|Ingresos del técnico (agrupado por mes)|

---

### 5.2 Resources

```java
public record HomeOwnerConsumptionResource(
    Long ownerId,
    int month,
    int year,
    double energyConsumed,
    double amountPaid,
    int serviceRequestsCount
) {}

public record TechnicianPerformanceResource(
    Long technicianId,
    int totalServicesCompleted,
    double averageRating,
    double averageCompletionTimeHours,
    int pendingServices
) {}

public record TechnicianRevenueResource(
    Long technicianId,
    String period,
    double totalRevenue,
    int servicesCount,
    double averageRevenuePerService
) {}
```

---

### 5.3 Assemblers

- `AnalyticsResourceFromEntityAssembler`: Métodos factory estáticos para construir recursos. Actualmente no se usa en el flujo principal (los recursos se construyen directamente en `AnalyticsQueryServiceImpl`).

---

<a name="flujos"></a>

## 6. Flujos CQRS por Caso de Uso

### 6.1 Consultar Consumo del Propietario

1. `AnalyticsController.getHomeOwnerConsumption` recibe `ownerId` y `months` (default 12).
2. `AnalyticsQueryServiceImpl.handle(GetHomeOwnerConsumptionQuery)`:
   - Consulta `RequestRepository.findByClientId()` con el `ownerId`.
   - Filtra solicitudes dentro del período solicitado.
   - Agrupa por (año, mes).
   - Para cada grupo, suma `energyConsumed` y `amountPaid` del `Bill` asociado.
3. Retorna `List<HomeOwnerConsumptionResource>` (una entrada por mes).

### 6.2 Consultar Desempeño del Técnico

1. `AnalyticsController.getTechnicianPerformance` recibe `technicianId`.
2. `AnalyticsQueryServiceImpl.handle(GetTechnicianPerformanceQuery)`:
   - Consulta `ServiceOperationRepository.findByTechnicianId()` con `TechnicianId`.
   - Filtra completados (`ServiceStatus.COMPLETED`) vs pendientes.
   - Calcula tiempo promedio de finalización (diferencia entre `startedAt` y `completedAt`).
   - Consulta `RatingRepository.findByTechnicianId()` y calcula rating promedio.
3. Retorna `List<TechnicianPerformanceResource>` (una entrada).

### 6.3 Consultar Ingresos del Técnico

1. `AnalyticsController.getTechnicianRevenue` recibe `technicianId` y `months` (default 6).
2. `AnalyticsQueryServiceImpl.handle(GetTechnicianRevenueQuery)`:
   - Consulta `ServiceOperationRepository.findByTechnicianId()` filtrado por `COMPLETED`.
   - Agrupa por (año, mes).
   - Para cada grupo, consulta `RequestRepository.findById()` para obtener el monto pagado (`Bill.amountPaid`).
   - Calcula ingresos totales, cantidad de servicios y promedio por servicio.
3. Retorna `List<TechnicianRevenueResource>` (una entrada por mes dentro del período).

---

<a name="integracion"></a>

## 7. Integración con otros BCs

### 7.1 SDP (Service Design and Planning)

- **Entrada (Inbound):** Analytics consulta `RequestRepository` (SDP) para:
  - Solicitudes por cliente (consumo del propietario).
  - Datos de facturación (`Bill`) para cálculos de ingresos.

### 7.2 Monitoring (Service Operation and Monitoring)

- **Entrada (Inbound):** Analytics consulta repositorios de Monitoring para:
  - `ServiceOperationRepository`: servicios completados, tiempos, estados.
  - `RatingRepository`: evaluaciones de técnicos.

### 7.3 Assets (Assets and Resource Management)

- **Entrada (Inbound):** Analytics tiene `PropertyRepository` inyectado (disponible para futura expansión, no usado actualmente).

### 7.4 Subscription BC

- Podría consultarse para verificar plan premium del usuario (acceso a analytics avanzados).

### 7.5 Consumidores de Analytics

Los datos de Analytics son consumidos por:
- **Propietarios**: Dashboard de consumo histórico, alertas por umbrales.
- **Técnicos**: Dashboard de desempeño, métricas de ingresos y rentabilidad.
- **Admin**: Visualizaciones interactivas y exportables de toda la plataforma.

---

**Supuestos declarados:**

1. **BC sin agregados propios**: Analytics es un BC de solo lectura que consulta datos de otros BCs mediante acceso directo a sus repositorios JPA.
2. **Sin eventos publicados**: No hay eventos de dominio propios; solo consume datos de otros BCs.
3. **Sin ACL propia**: No expone fachada hacia otros BCs; solo expone endpoints REST.
4. **Cálculos en tiempo real**: Las métricas se calculan en cada consulta. En el futuro se recomienda implementar proyecciones CQRS con tablas agregadas para mejorar rendimiento.
5. **Integración futura**: En una versión completa, este BC debería consumir eventos de servicios completados (SDP) para mantener datos agregados actualizados.
