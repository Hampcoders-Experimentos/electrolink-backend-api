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
📁 src/main/java/com/hampcoders/electrolink/subscription/
│
├── 📁 application/
│   └── 📁 internal/
│       ├── 📁 commandservices/
│       │   ├── SubscriptionCommandServiceImpl.java
│       │   └── PlanCommandServiceImpl.java
│       ├── 📁 queryservices/
│       │   ├── SubscriptionQueryServiceImpl.java
│       │   └── PlanQueryServiceImpl.java
│       └── 📁 eventhandlers/
│           ├── SubscriptionEventPublisher.java
│           └── PlanSeedOnStartup.java
│
├── 📁 domain/
│   ├── 📁 model/
│   │   ├── 📁 aggregates/
│   │   │   ├── Subscription.java
│   │   │   └── Plan.java
│   │   ├── 📁 entities/
│   │   │   └── PaymentTransaction.java
│   │   ├── 📁 valueobjects/
│   │   │   ├── SubscriptionStatus.java
│   │   │   ├── PlanType.java
│   │   │   └── Money.java
│   │   ├── 📁 commands/
│   │   │   ├── CreateSubscriptionCommand.java
│   │   │   ├── CancelSubscriptionCommand.java
│   │   │   ├── UpgradeSubscriptionCommand.java
│   │   │   ├── CreatePlanCommand.java
│   │   │   └── RecordPaymentCommand.java
│   │   ├── 📁 queries/
│   │   │   ├── GetSubscriptionByUserIdQuery.java
│   │   │   ├── GetActiveSubscriptionByUserIdQuery.java
│   │   │   ├── GetPlanByIdQuery.java
│   │   │   └── GetAllPlansQuery.java
│   │   └── 📁 events/
│   │       ├── SubscriptionActivatedEvent.java
│   │       ├── SubscriptionCancelledEvent.java
│   │       ├── PaymentProcessedEvent.java
│   │       └── RequestLimitReachedEvent.java
│   └── 📁 services/
│       ├── SubscriptionCommandService.java
│       ├── SubscriptionQueryService.java
│       ├── PlanCommandService.java
│       ├── PlanQueryService.java
│       └── PaymentGatewayService.java
│
├── 📁 infrastructure/
│   ├── 📁 persistence/jpa/repositories/
│   │   ├── SubscriptionRepository.java
│   │   ├── PlanRepository.java
│   │   └── PaymentTransactionRepository.java
│   └── 📁 payment/stripe/
│       └── StripePaymentGatewayService.java
│
└── 📁 interfaces/
    ├── 📁 acl/
    │   └── SubscriptionContextFacade.java
    └── 📁 rest/
        ├── SubscriptionsController.java
        ├── PlansController.java
        ├── 📁 resources/
        │   ├── CreateSubscriptionResource.java
        │   ├── SubscriptionResource.java
        │   ├── CreatePlanResource.java
        │   └── PlanResource.java
        └── 📁 transform/
            ├── CreateSubscriptionCommandFromResourceAssembler.java
            ├── SubscriptionResourceFromEntityAssembler.java
            ├── CreatePlanCommandFromResourceAssembler.java
            └── PlanResourceFromEntityAssembler.java
```

---

<a name="domain"></a>

## 2. Domain Layer

**Patrones aplicados (Domain):** Aggregate Root, Entity, Value Object, Domain Service (interfaces), Domain Events.

---

### 2.1 Aggregate Root: `Subscription`

**Ubicación:** `src/main/java/com/hampcoders/electrolink/subscription/domain/model/aggregates/Subscription.java`

**Responsabilidad:** Aggregate raíz que representa la suscripción de un usuario a un plan. Gestiona el estado de la suscripción, el conteo de solicitudes mensuales y los límites según el plan. Extiende `AuditableAbstractAggregateRoot<Subscription>`.

**Invariantes principales:**

- `userId` no puede ser null.
- `plan` es obligatorio (relación `@ManyToOne`).
- `status` puede ser `ACTIVE`, `CANCELLED`, `EXPIRED` o `PENDING`.
- `monthlyRequestCount` y `monthlyRequestResetDate` gestionan el límite mensual.
- El método `canMakeRequest()` verifica los límites del plan y reinicia el contador si pasó el mes.
- Al crear, publica `SubscriptionActivatedEvent`.
- Al cancelar, publica `SubscriptionCancelledEvent`.
- `isActive()` retorna `status == SubscriptionStatus.ACTIVE`.
- `isPremium()` delega en `plan.isPremium()`.

**Fragmento de implementación:**

```java
@Entity
@Table(name = "subscription_subscriptions")
@Getter
@NoArgsConstructor
public class Subscription extends AuditableAbstractAggregateRoot<Subscription> {

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column(nullable = false)
    private int monthlyRequestCount;

    @Column(nullable = false)
    private LocalDateTime monthlyRequestResetDate;

    public Subscription(Long userId, Plan plan) {
        this.userId = userId;
        this.plan = plan;
        this.status = SubscriptionStatus.ACTIVE;
        this.startDate = LocalDateTime.now();
        this.monthlyRequestCount = 0;
        this.monthlyRequestResetDate = LocalDateTime.now().plusMonths(1);
        registerEvent(new SubscriptionActivatedEvent(userId, plan.getId(), plan.getName()));
    }

    public boolean canMakeRequest() {
        resetMonthlyCountIfNeeded();
        return plan.canMakeRequest(monthlyRequestCount);
    }

    public void recordRequest() {
        resetMonthlyCountIfNeeded();
        if (!canMakeRequest()) {
            throw new IllegalStateException("Monthly request limit reached for plan: " + plan.getName());
        }
        monthlyRequestCount++;
    }

    public void upgradeTo(Plan newPlan) {
        this.plan = newPlan;
        this.monthlyRequestCount = 0;
        this.monthlyRequestResetDate = LocalDateTime.now().plusMonths(1);
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.endDate = LocalDateTime.now();
        registerEvent(new SubscriptionCancelledEvent(userId, plan.getId()));
    }

    private void resetMonthlyCountIfNeeded() {
        if (LocalDateTime.now().isAfter(monthlyRequestResetDate)) {
            monthlyRequestCount = 0;
            monthlyRequestResetDate = LocalDateTime.now().plusMonths(1);
        }
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    public boolean isPremium() {
        return plan.isPremium();
    }
}
```

---

### 2.2 Aggregate Root: `Plan`

**Ubicación:** `src/main/java/com/hampcoders/electrolink/subscription/domain/model/aggregates/Plan.java`

**Responsabilidad:** Catálogo de planes de suscripción con sus límites y beneficios. Extiende `AuditableAbstractAggregateRoot<Plan>`.

**Invariantes principales:**

- `name` es del tipo `PlanType` enum (BASIC, PREMIUM) y debe ser único.
- `price` es un `Money` value object embebido.
- `maxRequestsPerMonth` define el límite de solicitudes mensuales.
- `prioritySupport` indica si el plan tiene soporte prioritario.
- `isActive` indica si el plan está disponible.
- `canMakeRequest(int currentMonthlyRequests)` compara contra `maxRequestsPerMonth`.
- Métodos estáticos `createBasicPlan()` y `createPremiumPlan()` para crear planes por defecto.

**Fragmento de implementación:**

```java
@Entity
@Table(name = "subscription_plans")
@Getter
@NoArgsConstructor
public class Plan extends AuditableAbstractAggregateRoot<Plan> {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PlanType name;

    @Column(nullable = false)
    private String description;

    @Embedded
    private Money price;

    @Column(nullable = false)
    private int maxRequestsPerMonth;

    @Column(nullable = false)
    private boolean prioritySupport;

    @Column(nullable = false)
    private boolean isActive;

    public Plan(PlanType name, String description, Money price,
                int maxRequestsPerMonth, boolean prioritySupport) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.maxRequestsPerMonth = maxRequestsPerMonth;
        this.prioritySupport = prioritySupport;
        this.isActive = true;
    }

    public boolean canMakeRequest(int currentMonthlyRequests) {
        return currentMonthlyRequests < maxRequestsPerMonth;
    }

    public boolean isPremium() {
        return name == PlanType.PREMIUM;
    }

    public boolean isBasic() {
        return name == PlanType.BASIC;
    }

    public static Plan createBasicPlan() {
        return new Plan(PlanType.BASIC,
            "Free plan with limited monthly requests",
            Money.usd(0), 2, false);
    }

    public static Plan createPremiumPlan() {
        return new Plan(PlanType.PREMIUM,
            "Unlimited requests with priority support",
            Money.usd(29.99), Integer.MAX_VALUE, true);
    }
}
```

---

### 2.3 Entity: `PaymentTransaction`

**Ubicación:** `src/main/java/com/hampcoders/electrolink/subscription/domain/model/entities/PaymentTransaction.java`

**Responsabilidad:** Registro de transacciones de pago realizadas a través de la pasarela de pago. Extiende `AuditableModel`.

**Atributos:**
- `id` (Long, PK auto-generada)
- `subscriptionId` (Long, NOT NULL)
- `userId` (Long, NOT NULL)
- `amount` (Money, embebido)
- `paymentGatewayReference` (String, NOT NULL)
- `status` (String, NOT NULL)
- `paidAt` (LocalDateTime, NOT NULL)

---

### 2.4 Value Objects

|Value Object|Descripción|
|:--|:--|
|`SubscriptionStatus`|Enum: ACTIVE, CANCELLED, EXPIRED, PENDING|
|`PlanType`|Enum: BASIC, PREMIUM|
|`Money`|Value Object embedible con `BigDecimal amount` y `Currency currency`. Validación: amount no negativo. Método factory `Money.usd(double)`|

---

### 2.5 Commands (CQRS - Write)

```java
public record CreateSubscriptionCommand(Long userId, Long planId) {}
public record CancelSubscriptionCommand(Long userId) {}
public record UpgradeSubscriptionCommand(Long userId, Long newPlanId) {}
public record CreatePlanCommand(PlanType name, String description, double price,
    int maxRequestsPerMonth, boolean prioritySupport) {}
public record RecordPaymentCommand(Long userId, Long subscriptionId, Money amount,
    String paymentGatewayReference, String status) {}
```

---

### 2.6 Queries (CQRS - Read)

```java
public record GetSubscriptionByUserIdQuery(Long userId) {}
public record GetActiveSubscriptionByUserIdQuery(Long userId) {}
public record GetPlanByIdQuery(Long planId) {}
public record GetAllPlansQuery() {}
```

---

### 2.7 Domain Events

```java
public record SubscriptionActivatedEvent(Long userId, Long planId, PlanType planName) {}
public record SubscriptionCancelledEvent(Long userId, Long planId) {}
public record PaymentProcessedEvent(Long userId, Long subscriptionId, Money amount, String paymentReference) {}
public record RequestLimitReachedEvent(Long userId, int currentCount, int maxLimit) {}
```

---

### 2.8 Domain Services (interfaces)

|Servicio|Responsabilidad|
|:--|:--|
|`SubscriptionCommandService`|Interface: handle de commands de suscripción + `canUserMakeRequest(Long)` + `recordRequest(Long)`|
|`SubscriptionQueryService`|Interface: handle de queries de suscripción|
|`PlanCommandService`|Interface: handle de commands de planes + `seedDefaultPlans()` + `findById(Long)`|
|`PlanQueryService`|Interface: handle de queries de planes + `findByType(PlanType)`|
|`PaymentGatewayService`|Interface para integración con pasarela de pago (Stripe)|

---

<a name="application"></a>

## 3. Application Layer

---

### 3.1 Command Services

#### `SubscriptionCommandServiceImpl`

|Método|Comportamiento|
|:--|:--|
|`handle(CreateSubscriptionCommand)`|Si existe suscripción previa del usuario, hace upgrade. Si no, crea nueva.|
|`handle(UpgradeSubscriptionCommand)`|Cambia el plan del usuario, reinicia el contador de solicitudes.|
|`handle(CancelSubscriptionCommand)`|Cancela la suscripción activa, dispara evento.|
|`canUserMakeRequest(Long userId)`|Busca suscripción y retorna `subscription.canMakeRequest()`.|
|`recordRequest(Long userId)`|Busca suscripción y ejecuta `subscription.recordRequest()`.|

#### `PlanCommandServiceImpl`

|Método|Comportamiento|
|:--|:--|
|`handle(CreatePlanCommand)`|Crea un nuevo plan con `Money` value object (convierte `double` a `BigDecimal`).|
|`seedDefaultPlans()`|Crea planes BASIC y PREMIUM por defecto si no existen.|
|`findById(Long)`|Busca plan por ID.|

---

### 3.2 Query Services

- `SubscriptionQueryServiceImpl`: Busca suscripción por userId (todos o solo activa).
- `PlanQueryServiceImpl`: Busca plan por ID, lista todos los planes, busca por `PlanType`.

---

### 3.3 Event Handlers

- `SubscriptionEventPublisher`: Escucha y loggea `SubscriptionActivatedEvent` (se expandirá en el futuro para comunicación con otros BCs).
- `PlanSeedOnStartup`: Escucha `ApplicationReadyEvent` y ejecuta `planCommandService.seedDefaultPlans()`.

---

<a name="infrastructure"></a>

## 4. Infrastructure Layer

---

### 4.1 Repositorios JPA

|Repositorio|Extiende|Métodos adicionales|
|:--|:--|:--|
|`SubscriptionRepository`|`JpaRepository<Subscription, Long>`|`findByUserId(Long)`, `findByUserIdAndStatus(Long, SubscriptionStatus)`|
|`PlanRepository`|`JpaRepository<Plan, Long>`|`findByName(PlanType)`, `existsByName(PlanType)`|
|`PaymentTransactionRepository`|`JpaRepository<PaymentTransaction, Long>`|—|

---

### 4.2 Pasarela de Pago (Stripe)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/subscription/infrastructure/payment/stripe/StripePaymentGatewayService.java`

Implementa `PaymentGatewayService` con lógica mock:
- `createPayment()` genera un reference ID con prefijo `stripe_`.
- `verifyPayment()` siempre retorna `true`.

---

<a name="interfaces"></a>

## 5. Interfaces Layer

---

### 5.1 REST Controllers

**SubscriptionsController** (`/api/v1/subscriptions`)

|Método|Endpoint|Descripción|
|:--|:--|:--|
|`POST`|`/api/v1/subscriptions`|Crear suscripción (o upgrade si ya existe)|
|`GET`|`/api/v1/subscriptions/users/{userId}`|Obtener suscripción por userId|
|`GET`|`/api/v1/subscriptions/users/{userId}/active`|Obtener suscripción activa|
|`PUT`|`/api/v1/subscriptions/users/{userId}/upgrade/{planId}`|Actualizar plan|
|`DELETE`|`/api/v1/subscriptions/users/{userId}`|Cancelar suscripción|

**PlansController** (`/api/v1/plans`)

|Método|Endpoint|Descripción|
|:--|:--|:--|
|`GET`|`/api/v1/plans`|Listar todos los planes|
|`GET`|`/api/v1/plans/{planId}`|Obtener plan por ID|
|`POST`|`/api/v1/plans`|Crear plan|

---

### 5.2 Resources

```java
public record CreateSubscriptionResource(Long userId, Long planId) {}
public record SubscriptionResource(Long id, Long userId, Long planId, String planName,
    String status, LocalDateTime startDate, int monthlyRequestCount, boolean canMakeRequest) {}
public record CreatePlanResource(PlanType name, String description, double price,
    int maxRequestsPerMonth, boolean prioritySupport) {}
public record PlanResource(Long id, String name, String description, double price,
    int maxRequestsPerMonth, boolean prioritySupport, boolean isActive) {}
```

---

### 5.3 ACL (Fachada)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/subscription/interfaces/acl/SubscriptionContextFacade.java`

Clase concreta `@Service` que expone métodos para consumo por otros BCs:

```java
@Service
public class SubscriptionContextFacade {
    Long createSubscription(Long userId, Long planId);
    void upgradeSubscription(Long userId, Long newPlanId);
    void cancelSubscription(Long userId);
    boolean canUserMakeRequest(Long userId);
    void recordRequest(Long userId);
    Optional<Subscription> getActiveSubscription(Long userId);
    Optional<Long> getActiveSubscriptionPlanId(Long userId);
    boolean isPremiumUser(Long userId);
}
```

---

<a name="database"></a>

## 6. Esquema de Base de Datos

**Tabla:** `subscription_subscriptions`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|BIGINT|PK, AUTO_INCREMENT|
|`user_id`|BIGINT|NOT NULL|
|`plan_id`|BIGINT|NOT NULL, FK → subscription_plans.id|
|`status`|VARCHAR(20)|NOT NULL|
|`start_date`|TIMESTAMP|NOT NULL|
|`end_date`|TIMESTAMP|NULLABLE|
|`monthly_request_count`|INT|NOT NULL|
|`monthly_request_reset_date`|TIMESTAMP|NOT NULL|
|`created_at`|TIMESTAMP|NOT NULL (audit)|
|`updated_at`|TIMESTAMP|NOT NULL (audit)|

**Tabla:** `subscription_plans`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|BIGINT|PK, AUTO_INCREMENT|
|`name`|VARCHAR(20)|NOT NULL, UNIQUE|
|`description`|TEXT|NOT NULL|
|`amount`|DECIMAL(10,2)|NOT NULL (embebido de Money)|
|`currency`|VARCHAR(3)|NOT NULL (embebido de Money)|
|`max_requests_per_month`|INT|NOT NULL|
|`priority_support`|BOOLEAN|NOT NULL|
|`is_active`|BOOLEAN|NOT NULL|
|`created_at`|TIMESTAMP|NOT NULL (audit)|
|`updated_at`|TIMESTAMP|NOT NULL (audit)|

**Tabla:** `subscription_payment_transactions`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|BIGINT|PK, AUTO_INCREMENT|
|`subscription_id`|BIGINT|NOT NULL|
|`user_id`|BIGINT|NOT NULL|
|`amount`|DECIMAL(10,2)|NOT NULL (embebido de Money)|
|`currency`|VARCHAR(3)|NOT NULL (embebido de Money)|
|`payment_gateway_reference`|VARCHAR(100)|NOT NULL|
|`status`|VARCHAR(20)|NOT NULL|
|`paid_at`|TIMESTAMP|NOT NULL|
|`created_at`|TIMESTAMP|NOT NULL (audit)|
|`updated_at`|TIMESTAMP|NOT NULL (audit)|

---

<a name="flujos"></a>

## 7. Flujos CQRS por Caso de Uso

### 7.1 Crear Suscripción

1. `SubscriptionsController.createSubscription` recibe `CreateSubscriptionResource`.
2. `CreateSubscriptionCommandFromResourceAssembler.toCommandFromResource` crea el comando.
3. `SubscriptionCommandServiceImpl.handle(CreateSubscriptionCommand)`:
   - Busca suscripción existente por userId.
   - Si existe: ejecuta `subscription.upgradeTo(plan)` (re-asignación de plan).
   - Si no existe: crea nueva `Subscription`, publica `SubscriptionActivatedEvent`.
   - Persiste.
4. Retorna `SubscriptionResource` con status 201.

### 7.2 Cancelar Suscripción

1. `SubscriptionsController.cancelSubscription` recibe userId.
2. Crea `CancelSubscriptionCommand`.
3. `SubscriptionCommandServiceImpl.handle`:
   - Busca suscripción por userId.
   - Ejecuta `subscription.cancel()`.
   - Publica `SubscriptionCancelledEvent`.
4. Retorna 204 No Content.

### 7.3 Actualizar Plan (Upgrade)

1. `SubscriptionsController.upgradeSubscription` recibe userId y planId.
2. Crea `UpgradeSubscriptionCommand`.
3. `SubscriptionCommandServiceImpl.handle`:
   - Resuelve nuevo plan.
   - Busca suscripción por userId.
   - Ejecuta `subscription.upgradeTo(newPlan)`.
4. Retorna 200 OK.

### 7.4 Verificar Límite de Solicitudes

1. Otro BC (ej. SDP) llama `SubscriptionContextFacade.canUserMakeRequest(userId)`.
2. `SubscriptionCommandServiceImpl.canUserMakeRequest(userId)`:
   - Busca suscripción por userId.
   - Ejecuta `subscription.canMakeRequest()` (reinicia contador si pasó el mes).
   - Retorna boolean.

### 7.5 Registrar Solicitud

1. Otro BC llama `SubscriptionContextFacade.recordRequest(userId)`.
2. `SubscriptionCommandServiceImpl.recordRequest(userId)`:
   - Busca suscripción por userId.
   - Ejecuta `subscription.recordRequest()` (incrementa contador, lanza excepción si excede límite).
   - Persiste cambios.

### 7.6 Seed de Planes por Defecto

1. Al iniciar la aplicación, se dispara `ApplicationReadyEvent`.
2. `PlanSeedOnStartup.onApplicationReady()` llama `planCommandService.seedDefaultPlans()`.
3. `PlanCommandServiceImpl.seedDefaultPlans()` crea los planes BASIC (gratis, 2 solicitudes/mes) y PREMIUM ($29.99, ilimitado) si no existen.

---

<a name="integracion"></a>

## 8. Integración con otros BCs

### 8.1 SDP (Service Design and Planning)

- **Entrada (Inbound):** SDP BC consulta `SubscriptionContextFacade` para:
  - Validar si el usuario puede hacer solicitudes (`canUserMakeRequest`).
  - Verificar si es usuario premium (`isPremiumUser`) para solicitudes prioritarias.
  - Registrar solicitud después de crearla (`recordRequest`).
- **Salida:** SDP recibe eventos de suscripción para conocer el plan del usuario.

### 8.2 Profiles BC

- No hay integración directa en el código actual.

### 8.3 IAM BC

- No hay integración directa; la autenticación es resuelta por IAM.

### 8.4 Eventos hacia otros BCs

|Evento|Consumidores Potenciales|
|:--|:--|
|`SubscriptionActivatedEvent`|SDP BC, Analytics BC (loggeado por `SubscriptionEventPublisher`)|
|`SubscriptionCancelledEvent`|SDP BC|
|`RequestLimitReachedEvent`|SDP BC|
|`PaymentProcessedEvent`|Analytics BC|

---

**Supuestos declarados:**

1. El plan Básico (gratis) tiene un límite de 2 solicitudes mensuales; el Premium es ilimitado (`Integer.MAX_VALUE`).
2. Los planes se crean por defecto al iniciar la aplicación (`PlanSeedOnStartup`).
3. La pasarela de pago (Stripe) está configurada con implementación mock (genera reference ID, verify siempre true).
4. Si un usuario ya tiene suscripción y crea una nueva, se hace upgrade del plan (no se crea duplicado).
5. `RequestLimitReachedEvent` está definido pero no se publica actualmente en el código (el límite se valida con excepción).
6. El `SubscriptionEventPublisher` actualmente solo loggea eventos; no los propaga a otros BCs.
