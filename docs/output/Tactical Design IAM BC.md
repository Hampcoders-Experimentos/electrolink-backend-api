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
📁 src/main/java/com/hampcoders/electrolink/iam/                    # Bounded Context
    │
    ├── 📁 application/
    │   └── 📁 internal/
    │       ├── 📁 commandservices/
    │       │   ├── UserCommandServiceImpl.java
    │       │   └── RoleCommandServiceImpl.java
    │       ├── 📁 queryservices/
    │       │   ├── UserQueryServiceImpl.java
    │       │   └── RoleQueryServiceImpl.java
    │       ├── 📁 eventhandlers/
    │       │   └── ApplicationReadyEventHandler.java
    │       └── 📁 outboundservices/
    │           ├── hashing/HashingService.java
    │           └── tokens/TokenService.java
    │
    ├── 📁 domain/
    │   ├── 📁 model/
    │   │   ├── 📁 aggregates/
    │   │   │   └── User.java                                # Aggregate Root
    │   │   ├── 📁 entities/
    │   │   │   └── Role.java
    │   │   ├── 📁 valueobjects/
    │   │   │   └── Roles.java
    │   │   ├── 📁 commands/
    │   │   │   ├── SignInCommand.java
    │   │   │   ├── SignUpCommand.java
    │   │   │   └── SeedRolesCommand.java
    │   │   ├── 📁 queries/
    │   │   │   ├── GetAllUsersQuery.java
    │   │   │   ├── GetUserByIdQuery.java
    │   │   │   ├── GetUserByUsernameQuery.java
    │   │   │   ├── GetAllRolesQuery.java
    │   │   │   └── GetRoleByNameQuery.java
    │   │   └── 📁 events/
    │   │       └── UserRegisteredEvent.java
    │   └── 📁 services/
    │       ├── UserCommandService.java
    │       ├── UserQueryService.java
    │       ├── RoleCommandService.java
    │       └── RoleQueryService.java
    │
    ├── 📁 infrastructure/
    │   ├── 📁 authorization/
    │   │   └── 📁 sfs/
    │   │       ├── 📁 configuration/
    │   │       │   └── WebSecurityConfiguration.java
    │   │       ├── 📁 model/
    │   │       │   ├── UserDetailsImpl.java
    │   │       │   └── UsernamePasswordAuthenticationTokenBuilder.java
    │   │       ├── 📁 pipeline/
    │   │       │   ├── BearerAuthorizationRequestFilter.java
    │   │       │   └── UnauthorizedRequestHandlerEntryPoint.java
    │   │       └── 📁 services/
    │   │           └── UserDetailsServiceImpl.java
    │   ├── 📁 hashing/
    │   │   └── 📁 bcrypt/
    │   │       ├── BcryptHashingService.java
    │   │       └── 📁 services/
    │   │           └── HashingServiceImpl.java
    │   ├── 📁 persistence/
    │   │   └── 📁 jpa/
    │   │       └── 📁 repositories/
    │   │           ├── UserRepository.java
    │   │           └── RoleRepository.java
    │   └── 📁 tokens/
    │       └── 📁 jwt/
    │           ├── BearerTokenService.java
    │           └── 📁 services/
    │               └── TokenServiceImpl.java
    │
    └── 📁 interfaces/
        ├── 📁 acl/
        │   └── IamContextFacade.java
        └── 📁 rest/
            ├── AuthenticationController.java
            ├── UsersController.java
            ├── RolesController.java
            ├── 📁 resources/
            │   ├── AuthenticatedUserResource.java
            │   ├── SignInResource.java
            │   ├── SignUpResource.java
            │   ├── UserResource.java
            │   └── RoleResource.java
            └── 📁 transform/
                ├── AuthenticatedUserResourceFromEntityAssembler.java
                ├── SignInCommandFromResourceAssembler.java
                ├── SignUpCommandFromResourceAssembler.java
                ├── UserResourceFromEntityAssembler.java
                └── RoleResourceFromEntityAssembler.java
```

---

<a name="domain"></a>

## 2. Domain Layer

**Patrones aplicados (Domain):** Aggregate Root, Entity, Value Object, Enum, Domain Service (interfaces).

---

### 2.1 Aggregate Root: `User`

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/domain/model/aggregates/User.java`

**Responsabilidad:** Aggregate raíz del BC IAM. Representa al usuario autenticable del sistema y gestiona credenciales (`username` y `password` hasheado) y roles. Guarda internamente el password hasheado y los roles asignados, publica `UserRegisteredEvent` al crearse con roles.

**Invariantes principales:**

- `username` es único (constraint `@Column(unique = true)`).
- `password` no puede ser vacío (validaciones `@NotBlank` y `@Size`).
- `roles` es un conjunto de `Role` asignados al usuario (`@ManyToMany` con `FetchType.EAGER`).
- Al crear usuario con `List<Role>`, se ejecuta `Role.validateRoleSet()` que asigna `ROLE_CLIENT` por defecto si la lista está vacía.

#### Propiedades (resumen)

```java
@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User(String username, String password, List<Role> roles) {
        this(username, password);
        addRoles(roles);
        registerEvent(new UserRegisteredEvent(this.getId(), this.username));
    }

    public User addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoleSet);
        return this;
    }
}
```

#### Identificadores

**Estado actual (según código):** el ID es `Long` heredado de `AuditableAbstractAggregateRoot`.

No existen IDs tipados en el código actual.

---

### 2.2 Entities

|Entidad|Responsabilidad|
|:--|:--|
|`Role`|Entidad de referencia que representa un rol de seguridad del sistema. Almacena el nombre del rol como `Roles` enum y expone métodos de factoría (`getDefaultRole` → `ROLE_CLIENT`, `toRoleFromName`, `validateRoleSet`).|

---

### 2.3 Value Objects

```java
public enum Roles {
    ROLE_HOMEOWNER,
    ROLE_TECHNICIAN,
    ROLE_CLIENT
}
```

---

### 2.4 Commands (CQRS - Write)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/domain/model/commands`

```java
public record SignInCommand(String username, String password) {}
public record SignUpCommand(String username, String password, List<Role> roles) {}
public record SeedRolesCommand() {}
```

---

### 2.5 Queries (CQRS - Read)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/domain/model/queries`

```java
public record GetAllUsersQuery() {}
public record GetUserByIdQuery(Long userId) {}
public record GetUserByUsernameQuery(String username) {}
public record GetAllRolesQuery() {}
public record GetRoleByNameQuery(String name) {}
```

---

### 2.6 Domain Events

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/domain/model/events`

```java
public record UserRegisteredEvent(Long userId, String username) {}
```

---

### 2.7 Domain Services

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/domain/services`

|Servicio|Tipo|Responsabilidad|
|:--|:--|:--|
|`UserCommandService`|Puerto write|Handle de `SignInCommand` y `SignUpCommand`.|
|`UserQueryService`|Puerto read|Handle de `GetAllUsersQuery`, `GetUserByIdQuery`, `GetUserByUsernameQuery`.|
|`RoleCommandService`|Puerto write|Handle de `SeedRolesCommand` para carga inicial de roles.|
|`RoleQueryService`|Puerto read|Handle de `GetAllRolesQuery`, `GetRoleByNameQuery`.|

---

<a name="application"></a>

## 3. Application Layer

**Patrones aplicados (Application):** Command Service, Query Service, Event Handler, Outbound Services (hashing, tokens).

---

### 3.1 Command Services

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/application/internal/commandservices/`

#### `UserCommandServiceImpl`

**Orquestación principal:**

- `SignUpCommand`: valida unicidad de `username` via `UserRepository.existsByUsername`, resuelve roles desde `RoleRepository.findByName`, hashea password con `HashingService.encode`, crea `User`, persiste y devuelve `Optional<User>`.
- `SignInCommand`: busca usuario por `username` via `UserRepository.findByUsername`, compara password con `HashingService.matches`, genera JWT con `TokenService.generateToken`, devuelve `Optional<ImmutablePair<User, String>>`.

**Notas:**

- Lanza `RuntimeException` si `username` no existe o password no coincide.
- No se observa `@Transactional` explícito; se asume manejo por defecto.

#### `RoleCommandServiceImpl`

- `SeedRolesCommand`: itera sobre `Roles.values()`, verifica si el rol existe via `roleRepository.existsByName`, si no existe lo persiste.

---

### 3.2 Query Services

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/application/internal/queryservices/`

- `UserQueryServiceImpl`: delega en `UserRepository.findAll()`, `findById()`, `findByUsername()`.
- `RoleQueryServiceImpl`: delega en `RoleRepository.findAll()`, `findByName()`.

---

### 3.3 Event Handlers

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/application/internal/eventhandlers/ApplicationReadyEventHandler.java`

- Escucha `ApplicationReadyEvent` de Spring.
- Ejecuta `SeedRolesCommand` al arrancar la aplicación para sembrar `ROLE_HOMEOWNER`, `ROLE_TECHNICIAN`, `ROLE_CLIENT`.

---

### 3.4 Outbound Services

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/application/internal/outboundservices/`

|Servicio|Tipo|Propósito|Implementación|
|:--|:--|:--|:--|
|`HashingService`|Puerto outbound|Interfaz para hashing de passwords. Métodos: `encode`, `matches`.|`HashingServiceImpl` (BCrypt)|
|`TokenService`|Puerto outbound|Interfaz para generación y validación de JWT. Métodos: `generateToken`, `validateToken`, `getUsernameFromToken`.|`TokenServiceImpl` (jjwt)|

---

<a name="infrastructure"></a>

## 4. Infrastructure Layer

**Patrones aplicados (Infrastructure):** Repository (Spring Data JPA), BCrypt Hashing, JWT Tokens, Spring Security Filter Chain.

---

### 4.1 Persistencia (Spring Data JPA)

**Repositorios:**

|Repositorio|Extiende|Consultas derivadas clave|
|:--|:--|:--|
|`UserRepository`|`JpaRepository<User, Long>`|`findByUsername`, `existsByUsername`|
|`RoleRepository`|`JpaRepository<Role, Long>`|`findByName`, `existsByName`|

- El agregado `User` es `@Entity` y hereda `id`, `createdAt`, `updatedAt` de `AuditableAbstractAggregateRoot`.

---

### 4.2 Hashing

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/infrastructure/hashing/bcrypt/services/HashingServiceImpl.java`

- Implementa `HashingService` usando `BCryptPasswordEncoder` de Spring Security.
- Se inyecta como `PasswordEncoder` en la configuración de seguridad.

---

### 4.3 Tokens (JWT)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/infrastructure/tokens/jwt/services/TokenServiceImpl.java`

- Genera y valida JWT con `io.jsonwebtoken` (jjwt).
- Incluye claims: `username`.
- Expone `generateToken(username)`, `validateToken(token)`, `getUsernameFromToken(token)`.

---

### 4.4 Seguridad (Spring Security)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/infrastructure/authorization/sfs/**`

- `WebSecurityConfiguration` define `SecurityFilterChain` y registra el filtro `BearerAuthorizationRequestFilter`.
- `UserDetailsServiceImpl` adapta `User` a `UserDetails` para autenticación con `UsernamePasswordAuthenticationTokenBuilder`.
- `UnauthorizedRequestHandlerEntryPoint` maneja respuestas 401.
- Las rutas públicas son `/api/v1/authentication/**`; el resto requiere JWT válido.

---

<a name="interfaces"></a>

## 5. Interfaces Layer

**Patrones aplicados (Interfaces):** REST Controller, Resource DTO (sufijo `Resource`), Assembler, ACL Facade.

---

### 5.1 REST Controllers

**Ubicaciones:**

- `src/main/java/com/hampcoders/electrolink/iam/interfaces/rest/AuthenticationController.java`
- `src/main/java/com/hampcoders/electrolink/iam/interfaces/rest/UsersController.java`
- `src/main/java/com/hampcoders/electrolink/iam/interfaces/rest/RolesController.java`

**Endpoints expuestos:**

|Método|Ruta|Auth|Descripción|
|:--|:--|:--|:--|
|`POST`|`/api/v1/authentication/sign-up`|Público|Registro de usuario|
|`POST`|`/api/v1/authentication/sign-in`|Público|Login → devuelve JWT|
|`GET`|`/api/v1/users`|JWT|Listar todos los usuarios|
|`GET`|`/api/v1/users/{userId}`|JWT|Obtener usuario por ID|
|`GET`|`/api/v1/roles`|JWT|Listar todos los roles|

---

### 5.2 Resources (Request/Response)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/interfaces/rest/resources`

```java
public record SignInResource(String username, String password) {}
public record SignUpResource(String username, String password, List<String> roles) {}
public record UserResource(Long id, String username, List<String> roles) {}
public record AuthenticatedUserResource(Long id, String username, String token) {}
public record RoleResource(Long id, String name) {}
```

---

### 5.3 Assemblers (Transform)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/interfaces/rest/transform`

- `SignInCommandFromResourceAssembler`
- `SignUpCommandFromResourceAssembler`
- `UserResourceFromEntityAssembler`
- `AuthenticatedUserResourceFromEntityAssembler`
- `RoleResourceFromEntityAssembler`

---

### 5.4 ACL (Fachada Pública)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/iam/interfaces/acl/IamContextFacade.java`

Métodos expuestos a otros BCs (especialmente Profiles):

```java
@Service
public class IamContextFacade {
    public Long createUser(String username, String password);
    public Long createUser(String username, String password, List<String> roleNames);
    public Long fetchUserIdByUsername(String username);
    public String fetchUsernameByUserId(Long userId);
}
```

---

<a name="database"></a>

## 6. Esquema de Base de Datos

**Tablas derivadas del mapeo JPA:**

#### `users`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|BIGINT|PK, AUTO_INCREMENT|
|`username`|VARCHAR(50)|NOT NULL, UNIQUE|
|`password`|VARCHAR(120)|NOT NULL|
|`created_at`|TIMESTAMP|NOT NULL (audit)|
|`updated_at`|TIMESTAMP|NOT NULL (audit)|

#### `roles`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|BIGINT|PK, AUTO_INCREMENT|
|`name`|VARCHAR(20)|NOT NULL, UNIQUE|

#### `user_roles` (tabla join many-to-many)

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`user_id`|BIGINT|PK, FK → users.id|
|`role_id`|BIGINT|PK, FK → roles.id|

---

<a name="flujos"></a>

## 7. Flujos CQRS por Caso de Uso

### 7.1 Sign-Up (Registro)

1. `AuthenticationController.signUp` recibe `SignUpResource`.
2. `SignUpCommandFromResourceAssembler` crea `SignUpCommand`.
3. `UserCommandServiceImpl.handle(SignUpCommand)`:
   - Valida unicidad de `username` via `UserRepository.existsByUsername`.
   - Resuelve roles vía `RoleRepository.findByName`.
   - Hashea password via `HashingService.encode`.
   - Crea `User` con roles; se publica `UserRegisteredEvent`.
   - Persiste con `UserRepository.save`.
4. Devuelve `UserResource` con status 201.

### 7.2 Sign-In (Autenticación)

1. `AuthenticationController.signIn` recibe `SignInResource`.
2. `SignInCommandFromResourceAssembler` crea `SignInCommand`.
3. `UserCommandServiceImpl.handle(SignInCommand)`:
   - Busca usuario por `username` via `UserRepository.findByUsername`.
   - Verifica password con `HashingService.matches`.
   - Genera JWT con claims via `TokenService.generateToken`.
4. Devuelve `AuthenticatedUserResource` con status 200.

### 7.3 Consultar Usuarios

1. `UsersController.getAllUsers` crea `GetAllUsersQuery`.
2. `UserQueryServiceImpl.handle` recupera `List<User>` via `UserRepository.findAll`.
3. Transforma a `List<UserResource>` y retorna 200.

### 7.4 Consultar Usuario por ID

1. `UsersController.getUserById` crea `GetUserByIdQuery`.
2. `UserQueryServiceImpl.handle` recupera `Optional<User>` via `UserRepository.findById`.
3. Si no existe retorna 404; de lo contrario retorna `UserResource` con status 200.

### 7.5 Seed de Roles (arranque)

1. `ApplicationReadyEventHandler` escucha `ApplicationReadyEvent`.
2. Ejecuta `SeedRolesCommand` → `RoleCommandServiceImpl` itera `Roles.values()` y persiste los que no existan.

### 7.6 ACL — Crear Usuario desde otro BC

1. Profiles BC llama `IamContextFacade.createUser(username, password)`.
2. `IamContextFacade` construye `SignUpCommand` con `Role.getDefaultRole()`.
3. Delega en `UserCommandServiceImpl.handle`.
4. Retorna `userId` o 0L si falla.

---

<a name="integracion"></a>

## 8. Integración con otros BCs

### 8.1 Profiles BC

- **Desde Profiles hacia IAM (Inbound ACL):** Profiles BC usa `IamContextFacade` para verificar existencia de usuarios (`fetchUserIdByUsername`) y crear usuarios (`createUser`).
- **Desde IAM hacia Profiles (Outbound):** No existe en el código actual; IAM no consulta a Profiles.

### 8.2 Seguridad transversal

- El filtro `BearerAuthorizationRequestFilter` valida el JWT en cada request y carga `UserDetails` en el `SecurityContextHolder`, haciendo disponible la identidad del usuario autenticado para todos los BCs del monolito.
- `WebSecurityConfiguration` centraliza la definición de rutas públicas (`/api/v1/authentication/**`) y aplica protección JWT al resto.

### 8.3 Subscription, SDP, Assets, Analytics

- **No llaman directamente a IAM.** Reciben el `userId` como parte del JWT decodificado por el filtro de seguridad transversal.

---

**Supuestos declarados:**

- No existen IDs tipados en el código actual; se usa `Long` heredado de `AuditableAbstractAggregateRoot`.
- Los roles `ROLE_HOMEOWNER`, `ROLE_TECHNICIAN` y `ROLE_CLIENT` se siembran automáticamente al iniciar la aplicación.
- No se implementa refresh token en el MVP; la renovación se realiza mediante re-login.
- El evento `UserRegisteredEvent` se publica pero no hay consumidores definidos en el MVP.
- IAM no gestiona roles de negocio; solo gestiona roles de seguridad.
