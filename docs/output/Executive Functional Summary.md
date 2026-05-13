# Electrolink Platform — Executive Functional Summary

> **Documento:** Flujos funcionales completos del sistema, incluyendo actores, endpoints, comandos/queries, eventos y branches.

---

## 📚 ÍNDICE

1. [Diagrama General de Flujos](#diagrama)
2. [Flujo 1: Registro y Configuración Inicial](#flujo-1)
   - 1A: Sign-Up (Registro de Usuario)
   - 1B: Creación de Perfil
   - 1C: Creación de Suscripción
3. [Flujo 2: Configuración del Técnico](#flujo-2)
   - 2A: Creación de Servicios (Catálogo)
   - 2B: Creación de Inventario y Componentes
   - 2C: Configuración de Horarios
4. [Flujo 3: Configuración del Propietario](#flujo-3)
   - 3A: Registro de Propiedades
5. [Flujo 4: Solicitud de Servicio](#flujo-4)
   - 4A: Validación de Suscripción
   - 4B: Creación de Solicitud
   - 4C: Asignación Automática de Técnico
6. [Flujo 5: Monitoreo y Ejecución del Servicio](#flujo-5)
   - 5A: Creación de Service Operation
   - 5B: Actualización de Estados
   - 5C: Finalización y Evento de Completado
7. [Flujo 6: Post-Servicio](#flujo-6)
   - 6A: Reporte Técnico
   - 6B: Evaluación (Rating)
   - 6C: Descuento de Stock
8. [Flujo 7: Consultas Analíticas](#flujo-7)
   - 7A: Consumo del Propietario
   - 7B: Desempeño del Técnico
   - 7C: Ingresos del Técnico
9. [Mapa Completo de Actores vs BCs](#mapa-actores)
10. [Catálogo de Eventos de Dominio](#eventos)

---

<a name="diagrama"></a>

## 1. Diagrama General de Flujos

```
                    ┌──────────────────────────────────────────────────────────────────────┐
                    │                         ELECTROLINK PLATFORM                          │
                    └──────────────────────────────────────────────────────────────────────┘

    ACTOR                   FLUJO PRINCIPAL                          BCS INVOLUCADOS
    ─────                   ───────────────                          ───────────────

                    ┌─────────────────────────────────┐
 [USUARIO] ────────│ 1. REGISTRO Y CONFIGURACIÓN     │─── IAM, Profiles, Subscription
                    │    • Sign-Up → Perfil → Plan    │
                    └─────────────────────────────────┘

                    ┌─────────────────────────────────┐
 [TÉCNICO] ────────│ 2. CONFIGURACIÓN DEL TÉCNICO    │─── SDP, Assets
         │          │    • Servicios → Inventario     │
         │          │    • Horarios                   │
         │          └─────────────────────────────────┘
         │
         │          ┌─────────────────────────────────┐
 [HOMEOWNER] ──────│ 3. CONFIG. DEL PROPIETARIO      │─── Assets
                    │    • Propiedades                │
                    └─────────────────────────────────┘

                    ┌─────────────────────────────────┐
 [HOMEOWNER] ──────│ 4. SOLICITUD DE SERVICIO        │─── SDP, Subscription,
                    │    • Validar Plan → Crear Req.  │    Assets, Profiles
                    │    • Matching Automático        │
                    └─────────────────────────────────┘

                    ┌─────────────────────────────────┐
 [TÉCNICO] ────────│ 5. MONITOREO Y EJECUCIÓN        │─── Monitoring
                    │    • Estados → Reporte → Foto   │
                    └─────────────────────────────────┘

                    ┌─────────────────────────────────┐
 [AMBOS] ──────────│ 6. POST-SERVICIO                │─── Monitoring, Assets
                    │    • Rating → Descuento Stock   │
                    └─────────────────────────────────┘

                    ┌─────────────────────────────────┐
 [AMBOS] ──────────│ 7. ANALYTICS (CONSULTA)         │─── Analytics (reads
                    │    • Dashboard Prop./Téc.       │    SDP + Monitoring)
                    └─────────────────────────────────┘
```

---

<a name="flujo-1"></a>

## 2. Flujo 1: Registro y Configuración Inicial

### 2A. Sign-Up (Registro de Usuario)

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Usuario no registrado (futuro HomeOwner o Technician) |
| **BC** | IAM (Identity and Access Management) |
| **Endpoint** | `POST /api/v1/authentication/sign-up` (público) |
| **Request** | `SignUpResource { username, password, roles[] }` |
| **Command** | `SignUpCommand { String username, String password, List<Role> roles }` |
| **Query** | `GetRoleByNameQuery` (para resolver roles) |
| **Evento** | `UserRegisteredEvent { Long userId, String username }` |

**Flujo normal:**
```
1. Usuario envía POST /api/v1/authentication/sign-up con username + password + roles
2. IAM valida unicidad de username (UserRepository.existsByUsername)
3. IAM resuelve roles desde BD (RoleRepository.findByName)
4. IAM hashea password con BCrypt (HashingService.encode)
5. IAM crea User, publica UserRegisteredEvent
6. Responde 201: UserResource { id, username, roles }
```

**Branches:**
- **Username duplicado:** `UserRepository.existsByUsername` retorna true → lanza `RuntimeException("Username already exists!")`
- **Roles vacíos:** `Role.validateRoleSet` asigna `ROLE_CLIENT` por defecto
- **Rol no existe:** `RoleRepository.findByName` retorna vacío → lanza excepción

---

### 2B. Creación de Perfil

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Usuario autenticado (JWT) |
| **BC** | Profiles |
| **Endpoint** | `POST /api/v1/profiles` (JWT) |
| **Request** | `CreateProfileResource { firstName, lastName, email, street, role, additionalInfoOrCertification }` |
| **Command** | `CreateProfileCommand { firstName, lastName, email, street, role, additionalInfoOrCertification }` |
| **Integración** | → `InventoryContextFacade.createInventoryForTechnician()` (Assets BC) si rol = TECHNICIAN |

**Flujo normal:**
```
1. Usuario envía POST /api/v1/profiles con datos personales + rol
2. Profiles valida unicidad de email (ProfileRepository.existsByEmail_Address)
3. Profiles construye value objects: PersonName, EmailAddress, StreetAddress
4. Profiles crea Profile con HomeOwner o Technician según role
5. Si rol = TECHNICIAN → Profiles llama a Assets: crea TechnicianInventory vacío
6. Responde 201: ProfileResource
```

**Branches:**
- **Email duplicado:** `existsByEmail_Address` retorna true → lanza `IllegalArgumentException("Email already exists")`
- **Rol inválido:** Valores permitidos: `HOMEOWNER`, `TECHNICIAN` → lanza `IllegalArgumentException` si no coincide

---

### 2C. Creación de Suscripción

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Usuario autenticado (JWT) |
| **BC** | Subscription |
| **Endpoint** | `POST /api/v1/subscriptions` (JWT) |
| **Request** | `CreateSubscriptionResource { userId, planId }` |
| **Command** | `CreateSubscriptionCommand { Long userId, Long planId }` |
| **Evento** | `SubscriptionActivatedEvent { Long userId, Long planId, PlanType planName }` |

**Flujo normal:**
```
1. Usuario envía POST /api/v1/subscriptions con userId + planId
2. Subscription busca plan (PlanRepository.findById)
3. Si usuario ya tiene suscripción → hace upgrade (upgradeTo)
4. Si NO tiene → crea nueva Subscription (status = ACTIVE)
5. Publica SubscriptionActivatedEvent
6. Responde 201: SubscriptionResource { id, userId, planId, planName, status, startDate, monthlyRequestCount, canMakeRequest }
```

**Branches:**
- **Plan no existe:** `planRepository.findById` lanza `IllegalArgumentException("Plan not found")`
- **Suscripción existente:** Se reutiliza con `upgradeTo` (cambia plan, reinicia contador)
- **Seed automático:** Al iniciar app, `PlanSeedOnStartup` crea BASIC (gratis, 2 req/mes) y PREMIUM ($29.99, ilimitado)

---

<a name="flujo-2"></a>

## 3. Flujo 2: Configuración del Técnico

### 2A. Creación de Servicios (Catálogo)

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Technician (autenticado) |
| **BC** | SDP (Service Design and Planning) |
| **Endpoint** | `POST /api/v1/services` (JWT) |
| **Request** | `CreateServiceResource` |
| **Command** | `CreateServiceCommand { name, description, basePrice, estimatedTime, category, isVisible, createdBy, policy, restriction, tags, components }` |
| **Evento** | `ServiceCataloguedEvent { Long serviceId, String createdBy, String serviceName }` |
| **Entities** | `Tag` (etiquetas), `ComponentQuantity` (componentes requeridos con cantidad) |
| **Value Objects** | `Policy` (política del servicio), `Restriction` (restricciones) |

**Flujo normal:**
```
1. Technician envía POST /api/v1/services con datos del servicio
2. SDP crea ServiceEntity con tags y componentes requeridos
3. Publica ServiceCataloguedEvent
4. Responde 201 con ServiceEntity
```

**Branches:**
- **Actualización:** `PUT /api/v1/services/{serviceId}` → `UpdateServiceCommand` → busca y actualiza con `updateFrom()`
- **Eliminación:** `DELETE /api/v1/services/{serviceId}` → `DeleteServiceCommand` → verifica existencia, elimina

---

### 2B. Creación de Inventario y Componentes

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Technician (autenticado) o Profiles BC (automático) |
| **BC** | Assets |
| **Endpoint** | `POST /api/v1/technician-inventories` (JWT) |
| **Request** | `CreateTechnicianInventoryResource { technicianId }` |
| **Command** | `CreateTechnicianInventoryCommand { TechnicianId }` |

**Sub-flujo: Crear Tipo de Componente**

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `POST /api/v1/component-types` | Create | Crear tipo |
| `GET /api/v1/component-types` | List | Listar tipos |
| `GET /api/v1/component-types/{typeId}` | Get | Obtener tipo |

**Sub-flujo: Crear Componente**

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `POST /api/v1/components` | Create | Crear componente (con typeId) |
| `GET /api/v1/components` | List | Listar componentes |
| `GET /api/v1/components/{componentId}` | Get | Obtener |
| `PUT /api/v1/components/{componentId}` | Update | Actualizar |
| `DELETE /api/v1/components/{componentId}` | Delete | Eliminar |
| `GET /api/v1/components/search?name=` | Search | Buscar por nombre |

**Sub-flujo: Gestionar Stock en Inventario**

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `POST /api/v1/technician-inventories/{invId}/stock` | POST | Agregar componente al stock |
| `PATCH /.../stock/{componentId}` | PATCH | Actualizar cantidad/umbral |
| `DELETE /.../stock/{componentId}` | DELETE | Eliminar del stock |
| `GET /api/v1/technician-inventories/{technicianId}` | GET | Obtener inventario |
| `GET /api/v1/technician-inventories/low-stock` | GET | Alertas de stock bajo |

**Flujo normal de agregar stock:**
```
1. Technician agrega componente al inventario
2. Assets busca TechnicianInventory por ID
3. Ejecuta inventory.addToStock(component, quantity, threshold)
4. Responde ComponentStockResource
```

---

### 2C. Configuración de Horarios

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Technician (autenticado) |
| **BC** | SDP |
| **Endpoint** | `POST /api/v1/schedules` (JWT) |
| **Request** | `CreateScheduleResource { technicianId, date, startTime, endTime }` |
| **Command** | `CreateScheduleCommand { Long technicianId, LocalDate date, String startTime, String endTime }` |

**Endpoints de horarios:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/v1/schedules` | Crear horario |
| `GET` | `/api/v1/schedules/{scheduleId}` | Obtener por ID |
| `GET` | `/api/v1/schedules/technician/{technicianId}` | Listar por técnico |
| `PUT` | `/api/v1/schedules/{scheduleId}` | Actualizar |
| `DELETE` | `/api/v1/schedules/{scheduleId}` | Eliminar |

---

<a name="flujo-3"></a>

## 4. Flujo 3: Configuración del Propietario

### 3A. Registro de Propiedades

| Aspecto | Detalle |
|---------|---------|
| **Actor** | HomeOwner (autenticado) |
| **BC** | Assets |
| **Endpoint** | `POST /api/v1/properties` (JWT) |
| **Request** | `CreatePropertyResource { ownerId, address, region, district }` |
| **Command** | `CreatePropertyCommand { OwnerId, Address, Region, District }` |
| **Value Objects** | `OwnerId`, `Address`, `Region`, `District` |
| **ID Type** | UUID (generado automáticamente) |

**Flujo normal:**
```
1. HomeOwner envía POST /api/v1/properties
2. Assets construye Property con OwnerId, Address, Region, District embebidos
3. Persiste Property (ID = UUID)
4. Responde 201: PropertyResource
```

**Endpoints de propiedades:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/v1/properties` | Crear propiedad |
| `GET` | `/api/v1/properties` | Listar todas |
| `GET` | `/api/v1/properties/{propertyId}` | Obtener por ID (UUID) |
| `GET` | `/api/v1/properties/owner/{ownerId}` | Listar por propietario |
| `PUT` | `/api/v1/properties/{propertyId}` | Actualizar |
| `DELETE` | `/api/v1/properties/{propertyId}` | Eliminar |

---

<a name="flujo-4"></a>

## 5. Flujo 4: Solicitud de Servicio

### 4A. Validación de Suscripción (Pre-check)

| Aspecto | Detalle |
|---------|---------|
| **Actor** | SDP BC (llamada interna) |
| **BCs** | SDP → Subscription (vía ACL) |
| **ACL Method** | `SubscriptionContextFacade.canUserMakeRequest(Long userId)` |
| **ACL Method** | `SubscriptionContextFacade.isPremiumUser(Long userId)` |

**Flujo normal:**
```
SDP → SubscriptionContextFacade.canUserMakeRequest(userId)
  → SubscriptionCommandServiceImpl.canUserMakeRequest(userId)
    → subscriptionRepository.findByUserId(userId)
    → subscription.canMakeRequest()  // Reinicia contador si pasó el mes
    → retorna boolean
```

**Branches:**
- **Sin suscripción:** `findByUserId` retorna `Optional.empty()` → retorna `false`
- **Límite alcanzado:** `canMakeRequest()` retorna `false` cuando `monthlyRequestCount >= plan.maxRequestsPerMonth`
- **Plan premium:** `isPremiumUser()` → consulta `GetActiveSubscriptionByUserIdQuery` → `subscription.isPremium()`

---

### 4B. Creación de Solicitud

| Aspecto | Detalle |
|---------|---------|
| **Actor** | HomeOwner (autenticado) |
| **BC** | SDP |
| **Endpoint** | `POST /api/v1/requests` (JWT) |
| **Request** | `CreateRequestResource { clientId, technicianId, propertyId, serviceId, problemDescription, scheduledDate, bill, photos, priority }` |
| **Command** | `CreateRequestCommand { CreateRequestResource resource }` |
| **Evento** | `RequestCreatedEvent { Long requestId, Long clientId, String serviceId, boolean isPriority, String propertyId }` |

**Flujo normal:**
```
1. HomeOwner envía POST /api/v1/requests con datos de la solicitud
2. (Internamente SDP debiera validar Subscription BC - pendiente en MVP)
3. SDP crea Request con bill, photos, isPriority flag
4. Publica RequestCreatedEvent
5. Responde 201: Request
```

**Branches:**
- **Sin fotos:** `photos` puede ser null → se inicializa como lista vacía
- **Prioritaria:** Solo disponible para Premium; flag `isPriority` = true
- **Actualización:** `PUT /api/v1/requests/{id}` con `UpdateRequestCommand`
- **Eliminación:** `DELETE /api/v1/requests/{id}` con `DeleteRequestCommand`

---

### 4C. Asignación Automática de Técnico

| Aspecto | Detalle |
|---------|---------|
| **Trigger** | Interno (post-creación de Request) |
| **BC** | SDP (TechnicianMatchingService) |
| **Integraciones** | Profiles (cobertura), Assets (stock), SDP (horarios) |
| **ACLs usados** | `InventoryContextFacade`, `ExternalProfileService` |
| **Evento** | `ServiceAssignedEvent { Long requestId, Long technicianId }` |

**Flujo normal:**
```
1. Sistema detecta RequestCreatedEvent (o proceso interno)
2. TechnicianMatchingService.findBestTechnician(request):
   a. Consulta ubicación de la propiedad (Assets - PropertyRepository)
   b. Filtra técnicos por zona de cobertura (Profiles - ExternalProfileService)
   c. Verifica stock de componentes (Assets - InventoryContextFacade)
   d. Verifica disponibilidad en horarios (SDP - ScheduleRepository)
3. Asigna técnico a la Request (request.assignTechnician(technicianId))
4. Publica ServiceAssignedEvent
```

**Branches:**
- **Sin técnico disponible:** No hay técnico que cumpla todos los criterios
- **Prioridad:** Solicitudes prioritarias se procesan antes que las normales
- **Stock insuficiente:** El técnico no tiene los componentes necesarios → se salta

---

<a name="flujo-5"></a>

## 6. Flujo 5: Monitoreo y Ejecución del Servicio

### 5A. Creación de Service Operation

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Sistema (interno, post-asignación) |
| **BC** | Monitoring |
| **Endpoint** | `POST /api/v1/service-operations` (JWT) |
| **Request** | `CreateServiceOperationResource { requestId, technicianId, startedAt, currentStatus }` |
| **Command** | `CreateServiceOperationCommand { RequestId, TechnicianId, OffsetDateTime, ServiceStatus }` |
| **Value Objects** | `RequestId`, `TechnicianId` (embebidos) |

**Flujo normal:**
```
1. Sistema crea ServiceOperation con requestId + technicianId
2. Status inicial: PENDING
3. Responde 201: ServiceOperationResource
```

---

### 5B. Actualización de Estados

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Technician (autenticado) |
| **BC** | Monitoring |
| **Endpoint** | `PUT /api/v1/service-operations/status` (JWT) |
| **Request** | `UpdateServiceStatusResource { serviceOperationId, newStatus }` |
| **Command** | `UpdateServiceStatusCommand { Long serviceOperationId, ServiceStatus newStatus }` |

**Máquina de estados de `ServiceStatus`:**

```
        ┌──────────┐
        │ PENDING  │
        └────┬─────┘
             │
             ▼
        ┌───────────┐
        │IN_PROGRESS│
        └────┬──────┘
             │
       ┌─────┴─────┐
       │           │
       ▼           ▼
  ┌─────────┐ ┌─────────┐
  │COMPLETED│ │CANCELLED│
  └─────────┘ └─────────┘
```

**Flujo normal:**
```
1. Technician actualiza estado a IN_PROGRESS → COMPLETED
2. Al actualizar a COMPLETED:
   a. Se setea completedAt = OffsetDateTime.now()
   b. Se publica ServiceCompletedEvent { id, requestId, technicianId }
3. Responde 204 No Content
```

---

### 5C. Finalización y Evento de Completado

**Evento:** `ServiceCompletedEvent { Long serviceOperationId, Long requestId, Long technicianId }`

**Consumidores potenciales:**
- **Assets BC:** Escucha para descontar stock de componentes usados
- **Analytics BC:** Escucha para actualizar métricas en dashboards
- **Monitoring BC (Rating/Report):** Dispara flujo de post-servicio

---

<a name="flujo-6"></a>

## 7. Flujo 6: Post-Servicio

### 6A. Reporte Técnico

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Technician (autenticado) |
| **BC** | Monitoring |
| **Endpoint** | `POST /api/v1/reports` (JWT) |
| **Request** | `CreateReportResource { serviceOperationId, reportType, description }` |
| **Command** | `AddReportCommand { Long serviceOperationId, String reportType, String description }` |

**Flujo normal:**
```
1. Technician envía POST /api/v1/reports con datos del reporte
2. Monitoring crea Report asociado al serviceOperationId
3. Responde 201: reportId (Long)
```

**Sub-flujo: Fotos del Reporte**

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `POST /api/v1/report-photos` | POST | Agregar foto a un reporte |
| `POST /api/v1/report-photos/{reportPhotoId}` | POST | Add photo command (con reportId, url) |

**Branches:**
- **Tipo de reporte inválido:** Se valida en el assembler; si no es válido lanza `IllegalArgumentException`
- **Reportes por Service Operation:** `GET /api/v1/reports/requests/{serviceOperationId}`

---

### 6B. Evaluación (Rating)

| Aspecto | Detalle |
|---------|---------|
| **Actor** | HomeOwner (autenticado) |
| **BC** | Monitoring |
| **Endpoint** | `POST /api/v1/ratings` (JWT) |
| **Request** | `CreateRatingResource { requestId, technicianId, raterId, score, comment }` |
| **Command** | `AddRatingCommand` |
| **Constraints** | Score: 1-5; Comment: max 300 caracteres |

**Endpoints de Ratings:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/v1/ratings` | Crear rating |
| `PUT` | `/api/v1/ratings` | Actualizar score/comment |
| `DELETE` | `/api/v1/ratings/{ratingId}` | Eliminar |
| `GET` | `/api/v1/ratings/{ratingId}` | Obtener por ID |
| `GET` | `/api/v1/ratings` | Listar todos |
| `GET` | `/api/v1/ratings/technicians/{technicianId}` | Ratings por técnico |
| `GET` | `/api/v1/ratings/technicians/{technicianId}/featured` | Ratings destacados |
| `GET` | `/api/v1/ratings/requests/{requestId}` | Ratings por solicitud |

**Flujo normal:**
```
1. HomeOwner envía POST /api/v1/ratings con score (1-5) + comment opcional
2. Monitoring crea Rating con RequestId + TechnicianId embebidos
3. Responde 201: ratingId (Long)
```

---

### 6C. Descuento de Stock

| Aspecto | Detalle |
|---------|---------|
| **Trigger** | `ServiceCompletedEvent` (Monitoring → Assets) |
| **ACL** | `InventoryContextFacade.deductStock(technicianId, componentId, quantity)` |
| **BCs** | Monitoring → Assets |

**Flujo normal:**
```
1. Service se completa → Monitoring publica ServiceCompletedEvent
2. Assets escucha el evento (actualmente no implementado en código)
3. Assets descuenta componentes del TechnicianInventory
4. Si stock < threshold → alerta de stock bajo
```

---

<a name="flujo-7"></a>

## 8. Flujo 7: Consultas Analíticas

### 7A. Consumo del Propietario

| Aspecto | Detalle |
|---------|---------|
| **Actor** | HomeOwner (autenticado) |
| **BC** | Analytics |
| **Endpoint** | `GET /api/v1/analytics/homeowners/{ownerId}/consumption?months=12` (JWT) |
| **Query** | `GetHomeOwnerConsumptionQuery { Long ownerId, int months }` |
| **Service** | `AnalyticsQueryServiceImpl` |
| **Origen de datos** | `RequestRepository.findByClientId()` (SDP BC) |

**Flujo normal:**
```
1. HomeOwner solicita consumo con ownerId + months (default 12)
2. Analytics consulta RequestRepository.findByClientId()
3. Filtra solicitudes dentro del período (últimos N meses)
4. Agrupa por (año, mes)
5. Para cada grupo: suma energyConsumed + amountPaid del Bill asociado
6. Retorna List<HomeOwnerConsumptionResource>
```

**Response:** `List<HomeOwnerConsumptionResource { ownerId, month, year, energyConsumed, amountPaid, serviceRequestsCount }>`

---

### 7B. Desempeño del Técnico

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Technician (autenticado) |
| **BC** | Analytics |
| **Endpoint** | `GET /api/v1/analytics/technicians/{technicianId}/performance` (JWT) |
| **Query** | `GetTechnicianPerformanceQuery { Long technicianId }` |
| **Origen de datos** | `ServiceOperationRepository` + `RatingRepository` (Monitoring BC) |

**Flujo normal:**
```
1. Technician solicita métricas de desempeño
2. Analytics consulta ServiceOperationRepository.findByTechnicianId()
3. Filtra COMPLETADOS vs PENDIENTES
4. Calcula tiempo promedio (diferencia startedAt ↔ completedAt)
5. Consulta RatingRepository.findByTechnicianId() → promedio de scores
6. Retorna List<TechnicianPerformanceResource>
```

**Response:** `List<TechnicianPerformanceResource { technicianId, totalServicesCompleted, averageRating, averageCompletionTimeHours, pendingServices }>`

---

### 7C. Ingresos del Técnico

| Aspecto | Detalle |
|---------|---------|
| **Actor** | Technician (autenticado) |
| **BC** | Analytics |
| **Endpoint** | `GET /api/v1/analytics/technicians/{technicianId}/revenue?months=6` (JWT) |
| **Query** | `GetTechnicianRevenueQuery { Long technicianId, int months }` |
| **Origen de datos** | `ServiceOperationRepository` + `RequestRepository` (SDP BC) |

**Flujo normal:**
```
1. Technician solicita ingresos (periodo: últimos N meses)
2. Analytics consulta ServiceOperationRepository.findByTechnicianId()
3. Filtra COMPLETADOS con completedAt no nulo
4. Agrupa por (año, mes)
5. Para cada operación completada: consulta RequestRepository.findById()
   para obtener el Bill.amountPaid
6. Calcula totalRevenue, servicesCount, averageRevenuePerService
7. Retorna List<TechnicianRevenueResource> ordenado por periodo
```

**Response:** `List<TechnicianRevenueResource { technicianId, period, totalRevenue, servicesCount, averageRevenuePerService }>`

---

<a name="mapa-actores"></a>

## 9. Mapa Completo de Actores vs BCs

| Actor | IAM | Profiles | Subscription | Assets | SDP | Monitoring | Analytics |
|-------|:---:|:--------:|:------------:|:------:|:---:|:----------:|:---------:|
| **Usuario Anónimo** | Sign-Up, Sign-In | — | — | — | — | — | — |
| **HomeOwner** | — | Create/Update Profile | Create Subscription | CRUD Properties | Create Request, Get Status | Create Rating | Get Consumption |
| **Technician** | — | Create/Update Profile | Create Subscription | CRUD Inventory + Components | CRUD Services, Schedules | CRUD ServiceOperation, Report | Get Performance, Revenue |
| **Admin** | List Users, Roles | List Profiles | List Plans, Subscriptions | List All | List All | List All | — |
| **Sistema Interno** | — | — | Validate limits (ACL) | Check stock (ACL), Deduct | Technician Matching | Create ServiceOp | — |

---

<a name="eventos"></a>

## 10. Catálogo de Eventos de Dominio

| Evento | BC Origen | Publicado Cuando | Consumidores Potenciales |
|--------|-----------|-----------------|--------------------------|
| `UserRegisteredEvent { userId, username }` | IAM | Usuario se registra | Profiles (crear perfil automático) |
| `SubscriptionActivatedEvent { userId, planId, planName }` | Subscription | Suscripción creada o actualizada | SDP, Analytics |
| `SubscriptionCancelledEvent { userId, planId }` | Subscription | Suscripción cancelada | SDP |
| `PaymentProcessedEvent { userId, subscriptionId, amount, paymentReference }` | Subscription | Pago procesado | Analytics |
| `RequestLimitReachedEvent { userId, currentCount, maxLimit }` | Subscription | Límite de solicitudes alcanzado | SDP (mostrar upgrade) |
| `ServiceCataloguedEvent { serviceId, createdBy, serviceName }` | SDP | Servicio creado en catálogo | Analytics |
| `RequestCreatedEvent { requestId, clientId, serviceId, isPriority, propertyId }` | SDP | Solicitud creada | Subscription (registrar count), SDP (matching) |
| `ServiceAssignedEvent { requestId, technicianId }` | SDP | Técnico asignado automáticamente | Monitoring (crear ServiceOperation) |
| `ServiceCompletedEvent { serviceOperationId, requestId, technicianId }` | Monitoring | Servicio marcado como COMPLETED | Assets (descontar stock), Analytics |

---

## Apéndice: Mapa de Rutas de API por BC

### IAM (`/api/v1`)

| Método | Ruta | Auth | Actor |
|--------|------|:----:|:-----:|
| POST | `/authentication/sign-up` | Público | Anónimo |
| POST | `/authentication/sign-in` | Público | Anónimo |
| GET | `/users` | JWT | Admin |
| GET | `/users/{userId}` | JWT | Admin |
| GET | `/roles` | JWT | Admin |

### Profiles (`/api/v1`)

| Método | Ruta | Auth | Actor |
|--------|------|:----:|:-----:|
| POST | `/profiles` | JWT | Ambos |
| GET | `/profiles` | JWT | Admin |
| GET | `/profiles/{profileId}` | JWT | Ambos |
| PUT | `/profiles/{profileId}` | JWT | Ambos |
| DELETE | `/profiles/{profileId}` | JWT | Admin |
| GET | `/profiles/search` | JWT | Admin |

### Subscription (`/api/v1`)

| Método | Ruta | Auth | Actor |
|--------|------|:----:|:-----:|
| POST | `/subscriptions` | JWT | Ambos |
| GET | `/subscriptions/users/{userId}` | JWT | Ambos |
| GET | `/subscriptions/users/{userId}/active` | JWT | Ambos |
| PUT | `/subscriptions/users/{userId}/upgrade/{planId}` | JWT | Ambos |
| DELETE | `/subscriptions/users/{userId}` | JWT | Ambos |
| GET | `/plans` | JWT | Ambos |
| GET | `/plans/{planId}` | JWT | Ambos |
| POST | `/plans` | JWT | Admin |

### Assets (`/api/v1`)

| Método | Ruta | Auth | Actor |
|--------|------|:----:|:-----:|
| POST | `/properties` | JWT | HomeOwner |
| GET | `/properties` | JWT | Admin |
| GET | `/properties/{propertyId}` | JWT | Ambos |
| GET | `/properties/owner/{ownerId}` | JWT | HomeOwner |
| PUT | `/properties/{propertyId}` | JWT | HomeOwner |
| DELETE | `/properties/{propertyId}` | JWT | HomeOwner/Admin |
| POST | `/technician-inventories` | JWT | Technician/Sistema |
| GET | `/technician-inventories/{technicianId}` | JWT | Technician |
| GET | `/technician-inventories/low-stock` | JWT | Technician |
| POST | `/technician-inventories/{invId}/stock` | JWT | Technician |
| PATCH | `/technician-inventories/{invId}/stock/{compId}` | JWT | Technician |
| DELETE | `/technician-inventories/{invId}/stock/{compId}` | JWT | Technician |
| POST | `/components` | JWT | Technician/Admin |
| GET | `/components` | JWT | Ambos |
| GET | `/components/{componentId}` | JWT | Ambos |
| PUT | `/components/{componentId}` | JWT | Technician/Admin |
| DELETE | `/components/{componentId}` | JWT | Technician/Admin |
| GET | `/components/search?name=` | JWT | Ambos |
| POST | `/component-types` | JWT | Admin |
| GET | `/component-types` | JWT | Ambos |
| GET | `/component-types/{typeId}` | JWT | Ambos |
| PUT | `/component-types/{typeId}` | JWT | Admin |
| DELETE | `/component-types/{typeId}` | JWT | Admin |

### SDP (`/api/v1`)

| Método | Ruta | Auth | Actor |
|--------|------|:----:|:-----:|
| POST | `/services` | JWT | Technician |
| GET | `/services` | JWT | Ambos |
| GET | `/services/{serviceId}` | JWT | Ambos |
| PUT | `/services/{serviceId}` | JWT | Technician |
| DELETE | `/services/{serviceId}` | JWT | Technician/Admin |
| POST | `/requests` | JWT | HomeOwner |
| GET | `/requests/{id}` | JWT | Ambos |
| GET | `/requests/clients/{clientId}/requests` | JWT | HomeOwner |
| PUT | `/requests/{id}` | JWT | HomeOwner |
| DELETE | `/requests/{id}` | JWT | HomeOwner/Admin |
| POST | `/schedules` | JWT | Technician |
| GET | `/schedules/{scheduleId}` | JWT | Technician |
| GET | `/schedules/technician/{technicianId}` | JWT | Technician |
| PUT | `/schedules/{scheduleId}` | JWT | Technician |
| DELETE | `/schedules/{scheduleId}` | JWT | Technician |

### Monitoring (`/api/v1`)

| Método | Ruta | Auth | Actor |
|--------|------|:----:|:-----:|
| POST | `/service-operations` | JWT | Sistema |
| GET | `/service-operations` | JWT | Admin |
| GET | `/service-operations/{serviceOperationId}` | JWT | Ambos |
| GET | `/service-operations/technicians/{technicianId}` | JWT | Technician |
| PUT | `/service-operations/status` | JWT | Technician |
| POST | `/reports` | JWT | Technician |
| GET | `/reports` | JWT | Admin |
| GET | `/reports/{reportId}` | JWT | Ambos |
| GET | `/reports/requests/{serviceOperationId}` | JWT | Technician |
| DELETE | `/reports/{reportId}` | JWT | Technician/Admin |
| POST | `/ratings` | JWT | HomeOwner |
| PUT | `/ratings` | JWT | HomeOwner |
| DELETE | `/ratings/{ratingId}` | JWT | HomeOwner/Admin |
| GET | `/ratings` | JWT | Admin |
| GET | `/ratings/{ratingId}` | JWT | Ambos |
| GET | `/ratings/technicians/{technicianId}` | JWT | Ambos |
| GET | `/ratings/technicians/{technicianId}/featured` | JWT | Ambos |
| GET | `/ratings/requests/{requestId}` | JWT | Ambos |

### Analytics (`/api/v1`)

| Método | Ruta | Auth | Actor |
|--------|------|:----:|:-----:|
| GET | `/analytics/homeowners/{ownerId}/consumption?months=12` | JWT | HomeOwner |
| GET | `/analytics/technicians/{technicianId}/performance` | JWT | Technician |
| GET | `/analytics/technicians/{technicianId}/revenue?months=6` | JWT | Technician |

---

> **Fecha de generación:** Mayo 2026
> **Proyecto:** Electrolink Platform
> **Arquitectura:** Monolito Modular con DDD y CQRS
> **Propósito:** Este documento describe todos los flujos funcionales del sistema desde la perspectiva de los actores (HomeOwner, Technician, Admin, Sistema), incluyendo endpoints REST, comandos CQRS, queries, eventos de dominio y las integraciones entre Bounded Contexts.
