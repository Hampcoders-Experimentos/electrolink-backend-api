# Electrolink Platform — Spring Boot | DDD | CQRS | Monolito Modular

---

## 📚 ÍNDICE

1. [Estructura del Proyecto](#estructura)
2. [Domain Layer](#domain)
3. [Application Layer](#application)
4. [Infrastructure Layer](#infrastructure)
5. [Interfaces Layer](#interfaces)
6. [Esquema de Base de Datos](#database)
7. [Flujos CQRS por Caso de Uso](#flujos)
8. [Integración con otros BCs](#integracion)

---

<a name="estructura"></a>

## 1. Estructura del Proyecto

```
📁 src/main/java/com/hampcoders/electrolink/sdp/
│
├── 📁 application/
│   └── 📁 internal/
│       ├── 📁 commandservices/
│       │   ├── ServiceCommandServiceImpl.java
│       │   ├── RequestCommandServiceImpl.java
│       │   └── ScheduleCommandServiceImpl.java
│       ├── 📁 queryservices/
│       │   ├── ServiceQueryServiceImpl.java
│       │   ├── RequestQueryServiceImpl.java
│       │   └── ScheduleQueryServiceImpl.java
│       ├── 📁 outboundservices/
│       │   ├── IExternalProfileService.java
│       │   └── ExternalProfileService.java
│       └── 📁 services/
│           └── TechnicianMatchingService.java
│
├── 📁 domain/
│   ├── 📁 model/
│   │   ├── 📁 aggregates/
│   │   │   ├── ServiceEntity.java
│   │   │   ├── Request.java
│   │   │   └── ScheduleAggregate.java
│   │   ├── 📁 entities/
│   │   │   ├── Tag.java
│   │   │   ├── Photo.java
│   │   │   ├── ComponentQuantity.java
│   │   │   ├── Schedule.java
│   │   │   └── Bill.java
│   │   ├── 📁 valueobjects/
│   │   │   ├── Policy.java
│   │   │   └── Restriction.java
│   │   ├── 📁 commands/
│   │   │   ├── CreateServiceCommand.java
│   │   │   ├── UpdateServiceCommand.java
│   │   │   ├── DeleteServiceCommand.java
│   │   │   ├── CreateRequestCommand.java
│   │   │   ├── UpdateRequestCommand.java
│   │   │   ├── DeleteRequestCommand.java
│   │   │   ├── CreateScheduleCommand.java
│   │   │   ├── UpdateScheduleCommand.java
│   │   │   └── DeleteScheduleCommand.java
│   │   ├── 📁 queries/
│   │   │   ├── GetAllServicesQuery.java
│   │   │   ├── FindServiceByIdQuery.java
│   │   │   ├── FindRequestByIdQuery.java
│   │   │   ├── FindRequestsByClientIdQuery.java
│   │   │   ├── FindScheduleByIdQuery.java
│   │   │   └── FindSchedulesByTechnicianIdQuery.java
│   │   └── 📁 events/
│   │       ├── ServiceCataloguedEvent.java
│   │       ├── RequestCreatedEvent.java
│   │       └── ServiceAssignedEvent.java
│   └── 📁 services/
│       ├── ServiceCommandService.java
│       ├── ServiceQueryService.java
│       ├── RequestCommandService.java
│       ├── RequestQueryService.java
│       ├── ScheduleCommandService.java
│       └── ScheduleQueryService.java
│
├── 📁 infrastructure/
│   └── 📁 persistence/jpa/repositories/
│       ├── ServiceRepository.java
│       ├── RequestRepository.java
│       └── ScheduleRepository.java
│
└── 📁 interfaces/
    ├── 📁 acl/
    │   └── SdpContextFacade.java
    └── 📁 rest/
        ├── ServiceController.java
        ├── RequestController.java
        ├── ScheduleController.java
        ├── 📁 resources/
        │   ├── CreateServiceResource.java
        │   ├── CreateRequestResource.java
        │   ├── CreateScheduleResource.java
        │   ├── UpdateScheduleResource.java
        │   ├── TagResource.java
        │   ├── ScheduleResource.java
        │   ├── RestrictionResource.java
        │   ├── PolicyResource.java
        │   ├── ComponentQuantityResource.java
        │   └── BillResource.java
        └── 📁 transform/
            ├── ServiceMapper.java
            ├── RequestMapper.java
            └── ScheduleMapper.java
```

---

<a name="domain"></a>

## 2. Domain Layer

**Patrones aplicados (Domain):** Aggregate Root, Entity, Value Object, Domain Service (interfaces), Domain Events.

---

### 2.1 Aggregate Root: `ServiceEntity`

**Ubicación:** `src/main/java/com/hampcoders/electrolink/sdp/domain/model/aggregates/ServiceEntity.java`

**Responsabilidad:** Aggregate raíz que representa un servicio ofrecido en la plataforma. Gestiona la información del servicio, sus políticas, restricciones, componentes requeridos y etiquetas.

**Invariantes principales:**

- `name` y `description` son obligatorios.
- `basePrice` define el precio base del servicio.
- `isVisible` indica si el servicio está disponible para clientes.
- Al crear, publica `ServiceCataloguedEvent`.

**Fragmento de implementación:**

```java
@Entity
@Getter
@NoArgsConstructor
public class ServiceEntity extends AuditableAbstractAggregateRoot<ServiceEntity> {

    private String name;
    private String description;
    private Double basePrice;
    private String estimatedTime;
    private String category;
    private boolean isVisible;
    private String createdBy;

    @Embedded
    private Policy policy;

    @Embedded
    private Restriction restriction;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<ComponentQuantity> components = new ArrayList<>();

    public ServiceEntity(String name, String description, Double basePrice,
                         String estimatedTime, String category,
                         boolean isVisible, String createdBy, Policy policy, Restriction restriction,
                         List<Tag> tags, List<ComponentQuantity> components) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.estimatedTime = estimatedTime;
        this.category = category;
        this.isVisible = isVisible;
        this.createdBy = createdBy;
        this.policy = policy;
        this.restriction = restriction;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.components = components != null ? components : new ArrayList<>();
    }

    public void registerCreatedEvent() {
        registerEvent(new ServiceCataloguedEvent(this.getId(), this.createdBy, this.name));
    }
}
```

---

### 2.2 Aggregate Root: `Request`

**Ubicación:** `src/main/java/com/hampcoders/electrolink/sdp/domain/model/aggregates/Request.java`

**Responsabilidad:** Aggregate raíz que representa una solicitud de servicio realizada por un cliente. Gestiona la información del cliente, técnico asignado, propiedad, servicio, descripción del problema, fecha programada,facturación y fotos.

**Invariantes principales:**

- `clientId`, `propertyId`, `serviceId` son obligatorios.
- `isPriority` indica si es una solicitud prioritaria (solo para planes premium).
- Al crear, publica `RequestCreatedEvent`.

**Fragmento de implementación:**

```java
@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Request extends AuditableAbstractAggregateRoot<Request> {

    private String clientId;
    private String technicianId;
    private String propertyId;
    private String serviceId;
    private String problemDescription;
    private LocalDate scheduledDate;
    private boolean isPriority;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "bill_created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "bill_updated_at"))
    })
    private Bill bill;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "request_id")
    private List<Photo> photos = new ArrayList<>();

    public Request(String clientId, String technicianId, String propertyId, String serviceId,
                   String problemDescription, LocalDate scheduledDate,
                   Bill bill, List<Photo> photos, boolean isPriority) {
        this.clientId = clientId;
        this.technicianId = technicianId;
        this.propertyId = propertyId;
        this.serviceId = serviceId;
        this.problemDescription = problemDescription;
        this.scheduledDate = scheduledDate;
        this.bill = bill;
        this.photos = photos != null ? photos : new ArrayList<>();
        this.isPriority = isPriority;
    }

    public void assignTechnician(String technicianId) {
        this.technicianId = technicianId;
    }

    public void registerCreatedEvent() {
        registerEvent(new RequestCreatedEvent(this.getId(), Long.valueOf(this.clientId),
            this.serviceId, this.isPriority, this.propertyId));
    }
}
```

---

### 2.3 Aggregate Root: `ScheduleAggregate`

**Responsabilidad:** Gestiona la disponibilidad y horarios de los técnicos.

---

### 2.4 Entities

|Entidad|Responsabilidad|
|:--|:--|
|`Tag`|Etiquetas asociadas a servicios|
|`Photo`|Fotos adjuntas a las solicitudes|
|`ComponentQuantity`|Componentes requeridos para un servicio con cantidad|
|`Schedule`|Horario de disponibilidad del técnico|
|`Bill`|Información de facturación del recibo|

---

### 2.5 Value Objects

|Value Object|Descripción|
|:--|:--|
|`Policy`|Política del servicio (términos, garantías)|
|`Restriction`|Restricciones del servicio (requerimientos, condiciones)|

---

### 2.6 Commands (CQRS - Write)

```java
// Service commands
public record CreateServiceCommand(String name, String description, Double basePrice,
    String estimatedTime, String category, boolean isVisible, String createdBy,
    Policy policy, Restriction restriction, List<Tag> tags, List<ComponentQuantity> components) {}
public record UpdateServiceCommand(Long serviceId, ServiceEntity updated) {}
public record DeleteServiceCommand(Long serviceId) {}

// Request commands
public record CreateRequestCommand(CreateRequestResource resource) {}
public record UpdateRequestCommand(Long requestId, CreateRequestResource resource) {}
public record DeleteRequestCommand(Long requestId) {}

// Schedule commands
public record CreateScheduleCommand(Long technicianId, LocalDate date, String startTime, String endTime) {}
public record UpdateScheduleCommand(Long scheduleId, LocalDate date, String startTime, String endTime) {}
public record DeleteScheduleCommand(Long scheduleId) {}
```

---

### 2.7 Queries (CQRS - Read)

```java
public record GetAllServicesQuery() {}
public record FindServiceByIdQuery(Long serviceId) {}
public record FindRequestByIdQuery(Long requestId) {}
public record FindRequestsByClientIdQuery(String clientId) {}
public record FindScheduleByIdQuery(Long scheduleId) {}
public record FindSchedulesByTechnicianIdQuery(Long technicianId) {}
```

---

### 2.8 Domain Events

```java
public record ServiceCataloguedEvent(Long serviceId, String createdBy, String serviceName) {}
public record RequestCreatedEvent(Long requestId, Long clientId, String serviceId, boolean isPriority, String propertyId) {}
public record ServiceAssignedEvent(Long requestId, Long technicianId) {}
```

---

### 2.9 Domain Services

|Servicio|Responsabilidad|
|:--|:--|
|`ServiceCommandService`|Handle de commands de servicios|
|`ServiceQueryService`|Handle de queries de servicios|
|`RequestCommandService`|Handle de commands de solicitudes|
|`RequestQueryService`|Handle de queries de solicitudes|
|`ScheduleCommandService`|Handle de commands de horarios|
|`ScheduleQueryService`|Handle de queries de horarios|

---

<a name="application"></a>

## 3. Application Layer

---

### 3.1 Command Services

- `ServiceCommandServiceImpl`: Crear, actualizar, eliminar servicios.
- `RequestCommandServiceImpl`: Crear solicitudes, validar límites de suscripción.
- `ScheduleCommandServiceImpl`: Gestionar horarios de técnicos.

---

### 3.2 Query Services

- `ServiceQueryServiceImpl`: Consultar servicios con filtros.
- `RequestQueryServiceImpl`: Consultar solicitudes.
- `ScheduleQueryServiceImpl`: Consultar horarios.

---

### 3.3 Technician Matching Service

**Responsabilidad:** Servicio de dominio que implementa la lógica de matching entre solicitudes y técnicos disponibles. Considera:
- Ubicación geográfica (distrito de la propiedad)
- Stock de componentes disponibles
- Disponibilidad de horario

---

### 3.4 Outbound Services

- `ExternalProfileService`: Acceso a datos de perfil de técnicos (ubicación, zona de cobertura).

---

<a name="infrastructure"></a>

## 4. Infrastructure Layer

---

### 4.1 Repositorios JPA

|Repositorio|Extiende|
|:--|:--|
|`ServiceRepository`|`JpaRepository<ServiceEntity, Long>`|
|`RequestRepository`|`JpaRepository<Request, Long>`|
|`ScheduleRepository`|`JpaRepository<Schedule, Long>`|

---

<a name="interfaces"></a>

## 5. Interfaces Layer

---

### 5.1 REST Controllers

**ServiceController** (`/api/v1/services`)

|Método|Endpoint|Descripción|
|:--|:--|:--|
|`POST`|`/api/v1/services`|Crear servicio|
|`GET`|`/api/v1/services`|Listar servicios|
|`GET`|`/api/v1/services/{serviceId}`|Obtener servicio por ID|
|`PUT`|`/api/v1/services/{serviceId}`|Actualizar servicio|
|`DELETE`|`/api/v1/services/{serviceId}`|Eliminar servicio|

**RequestController** (`/api/v1/requests`)

|Método|Endpoint|Descripción|
|:--|:--|:--|
|`POST`|`/api/v1/requests`|Crear solicitud|
|`GET`|`/api/v1/requests/{id}`|Obtener solicitud por ID|
|`GET`|`/api/v1/requests/clients/{clientId}/requests`|Listar solicitudes por cliente|
|`PUT`|`/api/v1/requests/{id}`|Actualizar solicitud|
|`DELETE`|`/api/v1/requests/{id}`|Eliminar solicitud|

**ScheduleController** (`/api/v1/schedules`)

|Método|Endpoint|Descripción|
|:--|:--|:--|
|`POST`|`/api/v1/schedules`|Crear horario|
|`GET`|`/api/v1/schedules/{scheduleId}`|Obtener horario por ID|
|`GET`|`/api/v1/schedules/technician/{technicianId}`|Listar horarios por técnico|
|`PUT`|`/api/v1/schedules/{scheduleId}`|Actualizar horario|
|`DELETE`|`/api/v1/schedules/{scheduleId}`|Eliminar horario|

---

### 5.2 ACL (Interfaz de Fachada)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/sdp/interfaces/acl/SdpContextFacade.java`

Expone métodos para consumo por otros BCs:

```java
public interface SdpContextFacade {
    Optional<Long> findTechnicianForRequest(Long requestId);
    List<ServiceEntity> findAvailableServices(String district);
}
```

---

<a name="database"></a>

## 6. Esquema de Base de Datos

**Tabla:** `services`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|BIGINT|PK, AUTO_INCREMENT|
|`name`|VARCHAR(100)|NOT NULL|
|`description`|TEXT|NOT NULL|
|`base_price`|DECIMAL(10,2)|NOT NULL|
|`estimated_time`|VARCHAR(50)|NULLABLE|
|`category`|VARCHAR(50)|NOT NULL|
|`is_visible`|BOOLEAN|NOT NULL|
|`created_by`|VARCHAR(50)|NOT NULL|
|`created_at`|TIMESTAMP|NOT NULL (audit)|
|`updated_at`|TIMESTAMP|NOT NULL (audit)|

**Tabla:** `requests`

|Columna|T

ipo|Constraints|
|:--|:--|:--|
|`id`|BIGINT|PK, AUTO_INCREMENT|
|`client_id`|BIGINT|NOT NULL|
|`technician_id`|BIGINT|NULLABLE|
|`property_id`|UUID|NOT NULL|
|`service_id`|BIGINT|NOT NULL|
|`problem_description`|TEXT|NULLABLE|
|`scheduled_date`|DATE|NOT NULL|
|`is_priority`|BOOLEAN|NOT NULL|
|`created_at`|TIMESTAMP|NOT NULL (audit)|
|`updated_at`|TIMESTAMP|NOT NULL (audit)|

---

<a name="flujos"></a>

## 7. Flujos CQRS por Caso de Uso

### 7.1 Crear Servicio

1. `ServiceController.createService` recibe `CreateServiceResource`.
2. `ServiceMapper` crea el comando.
3. `ServiceCommandServiceImpl.handle`:
   - Crea el ServiceEntity.
   - Publica `ServiceCataloguedEvent`.
   - Persiste.
4. Retorna `ServiceEntity` con status 201.

### 7.2 Crear Solicitud de Servicio

1. `RequestController.createRequest` recibe `CreateRequestResource`.
2. `RequestMapper` crea el comando.
3. `RequestCommandServiceImpl.handle`:
   - Valida que el usuario tenga suscripción activa (consulta Subscription BC).
   - Valida el límite de solicitudes mensuales.
   - Crea la Request.
   - Publica `RequestCreatedEvent`.
4. Retorna `Request` con status 201.

### 7.3 Asignar Técnico Automáticamente

1. El sistema escucha `RequestCreatedEvent`.
2. `TechnicianMatchingService.findBestTechnician(request)`:
   - Consulta la ubicación de la propiedad (Assets BC).
   - Filtra técnicos por zona de cobertura (Profiles BC).
   - Verifica stock de componentes (Assets BC).
   - Verifica disponibilidad de horario.
3. Asigna el técnico a la solicitud.
4. Publica `ServiceAssignedEvent`.

---

<a name="integracion"></a>

## 8. Integración con otros BCs

### 8.1 Subscription BC

- **Entrada (Inbound):** SDP BC consulta `SubscriptionContextFacade` para validar:
  - Suscripción activa del usuario.
  - Límite de solicitudes mensuales no alcanzado.
  - Si el usuario tiene derecho a solicitudes prioritarias (plan premium).

### 8.2 Assets BC

- **Entrada (Inbound):** SDP BC consulta `InventoryContextFacade` para verificar disponibilidad de componentes.
- **Salida:** Al completar un servicio, SDP descuenta el stock de componentes usados.

### 8.3 Profiles BC

- **Entrada (Inbound):** SDP BC consulta `ExternalProfileService` para obtener zona de cobertura del técnico.

### 8.4 Eventos hacia otros BCs

|Evento|Consumidores|
|:--|:--|
|`RequestCreatedEvent`|Subscription BC (validar límites), SDP (matching)|
|`ServiceAssignedEvent`|Assets BC (reservar componentes)|
|`ServiceCataloguedEvent`|Analytics BC|

---

**Supuestos declarados:**

1. El matching de técnicos es automático basado en ubicación y disponibilidad.
2. Las solicitudes prioritarias solo están disponibles para usuarios con plan premium.
3. El flujo de validación de suscripción ocurre antes de crear la solicitud.
4. No hay validación de pago en el MVP; solo se verifica el plan.