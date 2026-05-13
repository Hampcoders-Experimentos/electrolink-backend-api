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
📁 src/main/java/com/hampcoders/electrolink/profiles/              # Bounded Context
    │
    ├── 📁 application/
    │   └── 📁 internal/
    │       ├── 📁 commandservices/
    │       │   └── ProfileCommandServiceImpl.java
    │       ├── 📁 queryservices/
    │       │   └── ProfileQueryServiceImpl.java
    │       └── 📁 outboundservices/
    │           ├── IExternalAssetsService.java
    │           └── ExternalAssetsService.java
    │
    ├── 📁 domain/
    │   ├── 📁 model/
    │   │   ├── 📁 aggregates/
    │   │   │   └── Profile.java                              # Aggregate Root
    │   │   ├── 📁 entities/
    │   │   │   ├── Technician.java
    │   │   │   └── HomeOwner.java
    │   │   ├── 📁 valueobjects/
    │   │   │   ├── PersonName.java
    │   │   │   ├── EmailAddress.java
    │   │   │   ├── StreetAddress.java
    │   │   │   └── Role.java
    │   │   ├── 📁 commands/
    │   │   │   ├── CreateProfileCommand.java
    │   │   │   ├── UpdateProfileCommand.java
    │   │   │   └── DeleteProfileCommand.java
    │   │   ├── 📁 queries/
    │   │   │   ├── GetAllProfilesQuery.java
    │   │   │   ├── GetProfileByIdQuery.java
    │   │   │   ├── GetProfileByEmailQuery.java
    │   │   │   ├── GetProfileByFullNameQuery.java
    │   │   │   └── GetProfilesByRoleQuery.java
    │   │   └── 📁 events/
    │   │       └── (sin eventos de dominio implementados en MVP)
    │   └── 📁 services/
    │       ├── ProfileCommandService.java
    │       └── ProfileQueryService.java
    │
    ├── 📁 infrastructure/
    │   └── 📁 persistence/
    │       └── 📁 jpa/
    │           └── 📁 repositories/
    │               └── ProfileRepository.java
    │
    └── 📁 interfaces/
        ├── 📁 acl/
        │   └── ProfilesContextFacade.java
        └── 📁 rest/
            ├── ProfilesController.java
            ├── 📁 resources/
            │   ├── CreateProfileResource.java
            │   └── ProfileResource.java
            └── 📁 transform/
                ├── CreateProfileCommandFromResourceAssembler.java
                ├── UpdateProfileCommandFromResourceAssembler.java
                └── ProfileResourceFromEntityAssembler.java
```

---

<a name="domain"></a>

## 2. Domain Layer

**Patrones aplicados (Domain):** Aggregate Root, Entity, Value Object, Domain Service (interfaces).

---

### 2.1 Aggregate Root: `Profile`

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/domain/model/aggregates/Profile.java`

**Responsabilidad:** Aggregate raíz del BC Profiles. Centraliza los datos funcionales del usuario dentro del dominio de Electrolink (nombre, email, dirección) y delega datos especializados a las entidades `Technician` o `HomeOwner`. Administra la asociación exclusiva a un rol de negocio mediante el enum `Role`.

**Invariantes principales:**

- Un perfil no puede ser `Technician` y `HomeOwner` simultáneamente.
- `email` debe ser único en el sistema (constraint y validación en aplicación).
- `role` es obligatorio desde la creación.
- `assignHomeOwner` solo se permite si `role == Role.HOMEOWNER`.
- `assignTechnician` solo se permite si `role == Role.TECHNICIAN`.

#### Propiedades (resumen)

```java
@Entity
@Table(name = "profiles")
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "firstName", column = @Column(name = "first_name", length = 30, nullable = false)),
        @AttributeOverride(name = "lastName", column = @Column(name = "last_name", length = 30, nullable = false))
    })
    private PersonName personName;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "street_address", length = 100, nullable = false))
    private StreetAddress address;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "email", length = 50, nullable = false, unique = true))
    private EmailAddress email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "home_owner_id")
    private HomeOwner homeOwner;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "technician_id")
    private Technician technician;

    public Profile(PersonName personName, EmailAddress email, StreetAddress address, Role role) {
        this.personName = personName;
        this.email = email;
        this.address = address;
        this.role = role;
    }

    public void assignHomeOwner(HomeOwner homeOwner) {
        if (role != Role.HOMEOWNER)
            throw new IllegalStateException("Cannot assign HomeOwner. Role must be HOMEOWNER.");
        this.homeOwner = homeOwner;
    }

    public void assignTechnician(Technician technician) {
        if (role != Role.TECHNICIAN)
            throw new IllegalStateException("Cannot assign Technician. Role must be TECHNICIAN.");
        this.technician = technician;
    }
}
```

#### Identificadores

**Estado actual (según código):** se usa `Long` como ID en `Profile`, `Technician` y `HomeOwner`.

No existen IDs tipados en el código actual.

---

### 2.2 Entidades y Value Objects

**Entidades:**

|Entidad|Responsabilidad|
|:--|:--|
|`Technician`|Datos específicos del técnico: `certificationCode` (código de certificación) y `isVerified` (estado de verificación).|
|`HomeOwner`|Datos específicos del propietario: `additionalInfo` (información adicional opcional).|

**Value Objects:**

```java
@Embeddable
public record PersonName(String firstName, String lastName) {
    public PersonName {
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("First name cannot be null or blank");
        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Last name cannot be null or blank");
    }
}

@Embeddable
public record EmailAddress(String address) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$", Pattern.CASE_INSENSITIVE);
    public EmailAddress {
        if (address == null || address.isBlank())
            throw new IllegalArgumentException("Email cannot be null or blank");
        if (!EMAIL_PATTERN.matcher(address).matches())
            throw new IllegalArgumentException("Invalid email address format");
    }
}

@Embeddable
public record StreetAddress(String street) {
    public StreetAddress {
        if (street == null || street.isBlank())
            throw new IllegalArgumentException("Address cannot be null or blank");
    }
}

public enum Role { HOMEOWNER, TECHNICIAN }
```

---

### 2.3 Commands (CQRS - Write)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/domain/model/commands`

```java
// Creación de perfil
public record CreateProfileCommand(
    String firstName,
    String lastName,
    String email,
    String street,
    Role role,
    String additionalInfoOrCertification
) {}

// Actualización
public record UpdateProfileCommand(
    Long profileId,
    String firstName,
    String lastName,
    String email,
    String street,
    Role role,
    String additionalInfoOrCertification
) {}

// Eliminación
public record DeleteProfileCommand(Long profileId) {}
```

---

### 2.4 Queries (CQRS - Read)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/domain/model/queries`

```java
// Perfil base
public record GetProfileByIdQuery(Long profileId) {}
public record GetProfileByEmailQuery(String email) {}
public record GetProfileByFullNameQuery(String firstName, String lastName) {}
public record GetAllProfilesQuery() {}
public record GetProfilesByRoleQuery(Role role) {}
```

---

### 2.5 Domain Services

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/domain/services`

|Servicio|Tipo|Responsabilidad|
|:--|:--|:--|
|`ProfileCommandService`|Puerto write|Handle de commands de creación, actualización y eliminación de `Profile`.|
|`ProfileQueryService`|Puerto read|Handle de queries sobre `Profile` y sus filtros.|

---

<a name="application"></a>

## 3. Application Layer

**Patrones aplicados (Application):** Command Service, Query Service, Outbound ACL.

---

### 3.1 Command Services

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/application/internal/commandservices/ProfileCommandServiceImpl.java`

**Orquestación principal:**

- `CreateProfileCommand`: valida unicidad de `email` via `ProfileRepository.existsByEmail_Address`; construye `PersonName`, `EmailAddress`, `StreetAddress`; crea `Profile` con `HomeOwner` o `Technician` según el rol; si el rol es `TECHNICIAN`, invoca `ExternalAssetsService.createInventoryForTechnician(profileId)` para inicializar inventario.
- `UpdateProfileCommand`: valida existencia del perfil y unicidad de email (excluyendo el perfil actual); actualiza datos del perfil y sus entidades hijas.
- `DeleteProfileCommand`: valida existencia y elimina el perfil.

**Notas:**
- Se define como `@Service`.
- No se observa `@Transactional` explícito; se asume manejo por defecto.

### 3.2 Query Services

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/application/internal/queryservices/ProfileQueryServiceImpl.java`

- `ProfileQueryServiceImpl`: delega en `ProfileRepository` para todas las queries de perfil con sus filtros.

### 3.3 Outbound Services (ACL hacia otros BCs)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/application/internal/outboundservices/`

|Servicio|BC destino|Propósito|
|:--|:--|:--|
|`ExternalAssetsService` (implementa `IExternalAssetsService`)|Assets|Notifica al BC Assets que se creó un Technician para que inicialice su inventario de componentes.|

---

<a name="infrastructure"></a>

## 4. Infrastructure Layer

**Patrones aplicados (Infrastructure):** Repository (Spring Data JPA), Value Object Embedding.

---

### 4.1 Persistencia (Spring Data JPA)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/infrastructure/persistence/jpa/repositories/ProfileRepository.java`

|Repositorio|Extiende|Consultas derivadas clave|
|:--|:--|:--|
|`ProfileRepository`|`JpaRepository<Profile, Long>`|`findByPersonName_FirstNameAndPersonName_LastName`, `findByEmail_Address`, `findByRole`, `existsByEmail_Address`, `existsByEmail_AddressAndIdIsNot`|

---

<a name="interfaces"></a>

## 5. Interfaces Layer

**Patrones aplicados (Interfaces):** REST Controller, Resource DTO (sufijo `Resource`), Assembler, ACL Facade.

---

### 5.1 REST Controllers

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/interfaces/rest/ProfilesController.java`

**Endpoints expuestos:**

|Método|Ruta|Auth|Descripción|
|:--|:--|:--|:--|
|`POST`|`/api/v1/profiles`|JWT|Crear perfil (HomeOwner o Technician)|
|`GET`|`/api/v1/profiles`|JWT|Listar todos los perfiles|
|`GET`|`/api/v1/profiles/{profileId}`|JWT|Obtener perfil por ID|
|`PUT`|`/api/v1/profiles/{profileId}`|JWT|Actualizar perfil|
|`DELETE`|`/api/v1/profiles/{profileId}`|JWT|Eliminar perfil|
|`GET`|`/api/v1/profiles/search`|JWT|Buscar perfiles por `email`, `role`, `firstName`+`lastName`|

---

### 5.2 Resources (Request/Response)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/interfaces/rest/resources`

```java
// Request resources
public record CreateProfileResource(String firstName, String lastName, String email, String street, String role, String additionalInfoOrCertification) {}
public record ProfileResource(Long id, String firstName, String lastName, String email, String street, String role, String additionalInfoOrCertification) {}
```

---

### 5.3 Assemblers (Transform)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/interfaces/rest/transform`

- `CreateProfileCommandFromResourceAssembler`
- `UpdateProfileCommandFromResourceAssembler`
- `ProfileResourceFromEntityAssembler`

---

### 5.4 ACL (Fachada Pública)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/profiles/interfaces/acl/ProfilesContextFacade.java`

Interfaz consumible por otros BCs. Expone operaciones CRUD completas de perfiles:

```java
@Service
public class ProfilesContextFacade {
    public Optional<ProfileResource> fetchProfileById(Long profileId);
    public Optional<ProfileResource> fetchProfileByEmail(String email);
    public List<ProfileResource> fetchProfilesByRole(Role role);
    public Long fetchProfileIdByEmail(String email);
    public boolean existsProfileByEmailAndIdIsNot(String email, Long id);
    public Long createProfile(String firstName, String lastName, String email, String street, Role role, String infoOrCert);
    public Long updateProfile(Long id, String firstName, String lastName, String email, String street, Role role, String infoOrCert);
    public void deleteProfile(Long profileId);
}
```

---

<a name="database"></a>

## 6. Esquema de Base de Datos

**Tablas principales (derivadas del mapeo JPA):**

#### `profiles`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|BIGINT|PK, AUTO_INCREMENT|
|`first_name`|VARCHAR(30)|NOT NULL|
|`last_name`|VARCHAR(30)|NOT NULL|
|`street_address`|VARCHAR(100)|NOT NULL|
|`email`|VARCHAR(50)|NOT NULL, UNIQUE|
|`role`|VARCHAR(20)|NOT NULL|
|`home_owner_id`|BIGINT|FK → `home_owners.id` (nullable)|
|`technician_id`|BIGINT|FK → `technicians.id` (nullable)|
|`created_at`|TIMESTAMP|NOT NULL (audit)|
|`updated_at`|TIMESTAMP|NOT NULL (audit)|

#### `home_owners`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|BIGINT|PK, AUTO_INCREMENT|
|`additional_info`|VARCHAR(100)|—|

#### `technicians`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|BIGINT|PK, AUTO_INCREMENT|
|`certification_code`|VARCHAR(50)|—|
|`is_verified`|BOOLEAN|—|

---

<a name="flujos"></a>

## 7. Flujos CQRS por Caso de Uso

### 7.1 Crear Perfil (HomeOwner o Technician)

1. `ProfilesController.createProfile` recibe `CreateProfileResource`.
2. `CreateProfileCommandFromResourceAssembler` crea `CreateProfileCommand`.
3. `ProfileCommandServiceImpl.handle(CreateProfileCommand)`:
   - Valida unicidad de `email` via `ProfileRepository.existsByEmail_Address`.
   - Construye `PersonName`, `EmailAddress`, `StreetAddress`.
   - Persiste `Profile` con `HomeOwner` o `Technician` según `role`.
   - Si `TECHNICIAN`: invoca `ExternalAssetsService.createInventoryForTechnician(profileId)`.
4. Retorna `ProfileResource` con status 201.

### 7.2 Actualizar Perfil

1. `ProfilesController.updateProfile` recibe `profileId` y `ProfileResource`.
2. `UpdateProfileCommandFromResourceAssembler` crea `UpdateProfileCommand`.
3. `ProfileCommandServiceImpl.handle`:
   - Valida existencia del perfil.
   - Valida unicidad de email (excluyendo el perfil actual).
   - Actualiza datos base y entidades hijas.
4. Retorna `ProfileResource` actualizado.

### 7.3 Eliminar Perfil

1. `ProfilesController.deleteProfile` recibe `profileId`.
2. `ProfileCommandServiceImpl.handle(DeleteProfileCommand)`:
   - Valida existencia del perfil.
   - Elimina el perfil en cascada.
3. Retorna 204 No Content.

### 7.4 Consultas (Read)

- **GetAllProfilesQuery** → `ProfileRepository.findAll()`.
- **GetProfileByIdQuery** → `ProfileRepository.findById()`.
- **GetProfileByEmailQuery** → `ProfileRepository.findByEmail_Address()`.
- **GetProfileByFullNameQuery** → `ProfileRepository.findByPersonName_FirstNameAndPersonName_LastName()`.
- **GetProfilesByRoleQuery** → `ProfileRepository.findByRole()`.

---

<a name="integracion"></a>

## 8. Integración con otros BCs

### 8.1 IAM BC

- **Entrada (Inbound ACL):** Profiles BC no consume IAM directamente en el MVP. La creación de usuario en IAM es independiente (se realiza a través de `IamContextFacade` si se necesita, pero el flujo actual de Electrolink no implementa este acoplamiento).
- **Salida (Outbound ACL):** `ProfilesContextFacade` expone operaciones CRUD de perfiles para ser consumidas por IAM u otros BCs cuando necesiten datos de perfil.

### 8.2 Assets BC

- **Salida (Outbound ACL):** `ExternalAssetsService.createInventoryForTechnician(technicianProfileId)` se invoca al crear perfiles de tipo `TECHNICIAN`, notificando a Assets BC que un nuevo técnico está disponible y necesita un inventario inicializado.
- **Punto de integración:** `InventoryContextFacade` de Assets BC es la interfaz consumida por `ExternalAssetsService`.

### 8.3 Subscription, SDP, Analytics

- **Sin integración directa desde Profiles.** Estos BCs reciben información del perfil a través del contexto de seguridad JWT (userId + roles).

---

**Supuestos declarados:**

- No hay eventos de dominio implementados en `domain/model/events`; las integraciones con otros BCs se realizan mediante llamadas directas a facades (ACL) en la capa de aplicación.
- IDs tipados (`ProfileId`, `TechnicianId`, `HomeOwnerId`) no existen en el código actual; se usa `Long`.
- La referencia a `user_id` de IAM no está presente en el aggregate `Profile` de Electrolink; la asociación se maneja a través del contexto de seguridad.
- Las entidades `Technician` y `HomeOwner` son entidades JPA independientes referenciadas desde `Profile` con `@OneToOne` y `CascadeType.ALL`.
