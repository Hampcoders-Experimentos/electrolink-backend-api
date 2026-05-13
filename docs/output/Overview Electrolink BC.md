# Electrolink Platform — Resumen de Bounded Contexts

## Resumen Ejecutivo

El proyecto Electrolink es una plataforma de servicios eléctricos que conecta propietarios de viviendas con técnicos electricistas. El sistema está construido como un **Monolito Modular** utilizando **Spring Boot**, aplicando principios de **Domain-Driven Design (DDD)** y **CQRS**.

La plataforma Actualmente implementa los siguientes **Bounded Contexts (BCs)**:

| Bounded Context | Ubicación | Estado |
|-----------------|------------|--------|
| **IAM** | `com.hampcoders.electrolink.iam` | ✅ Implementado |
| **Profiles** | `com.hampcoders.electrolink.profiles` | ✅ Implementado |
| **Subscription** | `com.hampcoders.electrolink.subscription` | ✅ Implementado |
| **Assets** | `com.hampcoders.electrolink.assets` | ✅ Implementado |
| **SDP** | `com.hampcoders.electrolink.sdp` | ✅ Implementado |
| **Analytics** | `com.hampcoders.electrolink.analytics` | ✅ MVP |

---

## 1. Bounded Contexts Detallados

### 1.1 Identity and Access Management (IAM)

**Propósito:** Gestionar la identidad digital, autenticación y autorización de usuarios.

**Aggregate Root:**
- `User`: Gestiona credenciales (username, password), roles y autenticación JWT.

**Entities:**
- `Role`: Catálogo de roles del sistema (TECHNICIAN, HOMEOWNER, ADMIN).

**Servicios clave:**
- Registro de usuarios (sign-up)
- Autenticación con JWT (sign-in)
- Gestión de roles

**Integraciones:**
- **Profiles BC**: Consulta roles de usuario
- **Subscription BC**: Validación de acceso

---

### 1.2 Profiles and Preferences

**Propósito:** Administrar perfiles de usuarios diferenciados por rol (Técnico/Propietario).

**Aggregate Root:**
- `Profile`: Datos centrales del usuario (nombre, email, dirección, rol).

**Entities:**
- `Technician`: Certificaciones, especialidades, zona de cobertura.
- `HomeOwner`: Información de contacto, preferencias.

**Value Objects:**
- `PersonName`, `EmailAddress`, `StreetAddress`, `Role`

**Servicios clave:**
- Crear/actualizar perfiles
- Búsqueda de perfiles por email, rol, nombre

**Integraciones:**
- **IAM BC**: userId como referencia
- **Assets BC**: Acceso a propiedades e inventario

---

### 1.3 Subscription and Payments

**Propósito:** Gestionar planes de suscripción, límites de uso y procesamiento de pagos.

**Aggregate Roots:**
- `Subscription`: Suscripción activa del usuario con plan.
- `Plan`: Catálogo de planes (Básico/Gratis, Premium).

**Value Objects:**
- `SubscriptionStatus` (ACTIVE, CANCELLED)
- `PlanType` (BASIC, PREMIUM)
- `Money`

**Domain Events:**
- `SubscriptionActivatedEvent`
- `SubscriptionCancelledEvent`
- `RequestLimitReachedEvent`

**Servicios clave:**
- Crear/cancelar suscripciones
- Upgrade de planes
- Verificación de límites de solicitudes
- Integración con Stripe (configurado)

**Integraciones:**
- **SDP BC**: Valida límites antes de crear solicitudes
- **Profiles BC**: Identifica tipo de usuario

---

### 1.4 Assets and Resource Management

**Propósito:** Gestionar recursos físicos diferenciados por tipo de usuario.

**Aggregate Roots:**
- `Property`: Propiedades del propietario con ubicación geográfica.
- `TechnicianInventory`: Inventario de componentes del técnico.
- `Component`: Catálogo de componentes disponibles.
- `ComponentType`: Tipos de componentes.

**Entities:**
- `ComponentStock`: Stock de un componente específico.

**Value Objects:**
- `Address`, `Region`, `District`, `OwnerId`, `TechnicianId`

**Servicios clave:**
- Gestionar propiedades
- Gestionar inventario de componentes
- Verificar disponibilidad de stock
- Alertas de stock bajo

**Integraciones:**
- **SDP BC**: Valida stock de componentes para servicios
- **Profiles BC**: Consulta propiedades del HomeOwner

---

### 1.5 Service Design and Planning (SDP)

**Propósito:** Orquestar el catálogo de servicios y el proceso de solicitud.

**Aggregate Roots:**
- `ServiceEntity`: Catálogo de servicios con recetas de componentes.
- `Request`: Solicitudes de servicio de los clientes.
- `ScheduleAggregate`: Disponibilidad de técnicos.

**Entities:**
- `Tag`, `Photo`, `ComponentQuantity`, `Schedule`, `Bill`

**Value Objects:**
- `Policy`, `Restriction`

**Domain Events:**
- `ServiceCataloguedEvent`
- `RequestCreatedEvent`
- `ServiceAssignedEvent`

**Servicios clave:**
- Crear servicios con componentes requeridos
- Crear solicitudes de servicio
- Matching automático de técnicos
- Gestionar horarios de técnicos

**Integraciones:**
- **Subscription BC**: Valida límites de solicitudes
- **Assets BC**: Verifica stock de componentes
- **Profiles BC**: Obtiene zona de cobertura del técnico
- **Analytics BC**: Recibe eventos de servicios completados

---

### 1.6 Analytics

**Propósito:** Proveer visualizaciones e insights basados en datos de consumo y servicios.

**Estado:** MVP - Solo consultas (read-only), sin persistencia propia.

**Consultas disponibles:**
- `GetHomeOwnerConsumptionQuery`: Consumo histórico del propietario
- `GetTechnicianPerformanceQuery`: Métricas de desempeño del técnico
- `GetTechnicianRevenueQuery`: Ingresos y rentabilidad del técnico

**Servicios clave:**
- Dashboard de consumo para propietarios
- Métricas de desempeño para técnicos

**Integraciones (futuro):**
- **SDP BC**: Consume eventos de servicios completados
- **Subscription BC**: Datos de planes premium

---

## 2. Flujo Principal del Sistema

### 2.1 Registro y Configuración

1. **Usuario se registra** en IAM (`/api/v1/authentication/sign-up`)
2. **Crea su perfil** en Profiles (`/api/v1/profiles`)
3. **Elige un plan** de suscripción en Subscription (`/api/v1/subscriptions`)

### 2.2 Flujo del Técnico

1. **Crea su catálogo de servicios** en SDP (`/api/v1/services`)
2. **Configura su inventario** de componentes en Assets
3. **Define su disponibilidad** horaria en SDP

### 2.3 Flujo del Propietario

1. **Registra sus propiedades** en Assets (`/api/v1/properties`)
2. **Inicia solicitud de servicio** en SDP (`/api/v1/requests`)
3. **Sistema valida suscripción** en Subscription
4. **Sistema asigna técnico** automáticamente (matching)
5. **Técnico ejecuta servicio** y registra resultados

### 2.4 Post-Servicio

1. **Stock de componentes se descuenta** en Assets
2. **Dashboard se actualiza** en Analytics

---

## 3. Integraciones Entre Bounded Contexts

### Diagrama de Integración

```
IAM BC
   ↑
   │ (userId)
   │
Profiles BC ─────────────────────────────────────────┐
   ↑                                                  │
   │ (role, technicianId, homeOwnerId)               │
   │                                                  │
   ↓                                                  ▼
Subscription BC ←───────────────────────────────── SDP BC
   ↑                     (validate limits)            ↑
   │                                                  │ (stock check)
   │                                                  ▼
   │                                              Assets BC
   │                                                  ↑
   │ (consumption data)                              │ (components)
   │                                                  │
   └──────────────────────────────────→ Analytics BC
```

### Integraciones Clave

| De BC | Hacia BC | Propósito | Método |
|-------|----------|-----------|--------|
| SDP | Subscription | Validar límites | ACL (`SubscriptionContextFacade`) |
| SDP | Assets | Verificar stock | ACL (`InventoryContextFacade`) |
| SDP | Profiles | Zona de cobertura | Outbound Service |
| Profiles | Assets | Propiedades del owner | Outbound Service |
| Subscription | SDP | Notificar límites | Event (`RequestLimitReachedEvent`) |
| SDP | Analytics | Métricas de servicio | Event (`ServiceCataloguedEvent`) |

---

## 4. Tecnologías y Patrones

### Tecnologías

- **Framework**: Spring Boot 3.x
- **Persistencia**: Spring Data JPA (Hibernate)
- **Seguridad**: Spring Security con JWT
- **API**: RESTful
- **Base de datos**: PostgreSQL (por defecto)

### Patrones Aplicados

| Patrón | Descripción |
|--------|-------------|
| **DDD** | Bounded contexts con agregados, entidades, value objects |
| **CQRS** | Separación de Commands (write) y Queries (read) |
| **Domain Events** | Publicación de eventos para integración entre BCs |
| **ACL** | Anti-Corruption Layer para comunicación entre BCs |
| **Aggregate Root** | Entidades raíz que encapsulan invariantes de negocio |

---

## 5. Rutas de API Principales

| BC | Endpoint | Métodos |
|----|----------|---------|
| **IAM** | `/api/v1/authentication` | POST (sign-in, sign-up) |
| | `/api/v1/users` | GET, GET/{id} |
| | `/api/v1/roles` | GET |
| **Profiles** | `/api/v1/profiles` | POST, GET, PUT, DELETE |
| | `/api/v1/profiles/search` | GET |
| **Subscription** | `/api/v1/subscriptions` | POST, GET, PUT, DELETE |
| | `/api/v1/plans` | POST, GET |
| **Assets** | `/api/v1/properties` | POST, GET, PUT, DELETE |
| | `/api/v1/technician-inventories` | POST, GET |
| | `/api/v1/components` | POST, GET, PUT, DELETE |
| | `/api/v1/component-types` | POST, GET, PUT, DELETE |
| **SDP** | `/api/v1/services` | POST, GET, PUT, DELETE |
| | `/api/v1/requests` | POST, GET, PUT, DELETE |
| | `/api/v1/schedules` | POST, GET, PUT, DELETE |
| **Analytics** | `/api/v1/analytics/homeowner/{id}/consumption` | GET |
| | `/api/v1/analytics/technician/{id}/performance` | GET |
| | `/api/v1/analytics/technician/{id}/revenue` | GET |

---

## 6. Estado de Implementación

| Bounded Context | Completitud | Notas |
|-----------------|-------------|-------|
| **IAM** | 90% | JWT, roles, seed automático |
| **Profiles** | 70% | Entidades Technician/HomeOwner no completas |
| **Subscription** | 80% | Planes, límites, eventos; Stripe no activo |
| **Assets** | 85% | Propiedades, inventario, componentes |
| **SDP** | 75% | Servicios, solicitudes; matching básico |
| **Analytics** | 30% | Solo endpoints mockeados |

---

## 7. Recomendaciones para Evolución Futura

1. **Completar Analytics**: Implementar persistencia y consumo de eventos para dashboards reales.
2. **Complementos de Profiles**: Completar entidades Technician y HomeOwner con más campos.
3. **Service Operation**: Considerar un nuevo BC para seguimiento de servicios en tiempo real.
4. **Monitoring**: Expandir el BC de monitoring para métricas de salud del sistema.
5. **Eventos**: Implementar consumo de eventos para actualizar Analytics en tiempo real.

---

## 8. Archivos Generados

Los siguientes documentos Tactical Design han sido generados:

1. `Tactical Design IAM BC.md`
2. `Tactical Design Profiles BC.md`
3. `Tactical Design Subscription BC.md`
4. `Tactical Design Assets BC.md`
5. `Tactical Design SDP BC.md`
6. `Tactical Design Analytics BC.md`
7. `Overview Electrolink BC.md` (este documento)

---

**Fecha de generación:** Mayo 2026
**Proyecto:** Electrolink Platform
**Arquitectura:** Monolito Modular con DDD y CQRS