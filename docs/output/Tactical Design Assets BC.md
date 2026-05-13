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
📁 src/main/java/com/hampcoders/electrolink/assets/
│
├── 📁 application/
│   └── 📁 internal/
│       ├── 📁 commandservices/
│       │   ├── PropertyCommandServiceImpl.java
│       │   ├── TechnicianInventoryCommandServiceImpl.java
│       │   ├── ComponentCommandServiceImpl.java
│       │   └── ComponentTypeCommandServiceImpl.java
│       └── 📁 queryservices/
│           ├── PropertyQueryServiceImpl.java
│           └── TechnicianInventoryQueryServiceImpl.java
│
├── 📁 domain/
│   ├── 📁 model/
│   │   ├── 📁 aggregates/
│   │   │   ├── Property.java
│   │   │   ├── TechnicianInventory.java
│   │   │   ├── Component.java
│   │   │   └── ComponentType.java
│   │   ├── 📁 entities/
│   │   │   └── ComponentStock.java
│   │   ├── 📁 valueobjects/
│   │   │   ├── Address.java
│   │   │   ├── Region.java
│   │   │   ├── District.java
│   │   │   ├── OwnerId.java
│   │   │   ├── TechnicianId.java
│   │   │   ├── ComponentId.java
│   │   │   ├── ComponentTypeId.java
│   │   │   ├── InventoryStockList.java
│   │   │   ├── PropertyStatuses.java
│   │   │   └── PropertyPhoto.java
│   │   ├── 📁 commands/
│   │   │   ├── CreatePropertyCommand.java
│   │   │   ├── UpdatePropertyCommand.java
│   │   │   ├── DeletePropertyCommand.java
│   │   │   ├── CreateTechnicianInventoryCommand.java
│   │   │   ├── DeleteTechnicianInventoryCommand.java
│   │   │   ├── UpdateComponentStockCommand.java
│   │   │   ├── AddComponentStockCommand.java
│   │   │   ├── CreateComponentCommand.java
│   │   │   ├── UpdateComponentCommand.java
│   │   │   ├── DeleteComponentCommand.java
│   │   │   ├── CreateComponentTypeCommand.java
│   │   │   ├── UpdateComponentTypeCommand.java
│   │   │   └── DeleteComponentTypeCommand.java
│   │   └── 📁 queries/
│   │       ├── GetAllPropertiesQuery.java
│   │       ├── GetPropertyByIdQuery.java
│   │       ├── GetAllPropertiesByOwnerIdQuery.java
│   │       ├── GetInventoryByTechnicianIdQuery.java
│   │       ├── GetInventoriesWithLowStockQuery.java
│   │       ├── GetComponentByIdQuery.java
│   │       ├── GetComponentsByNameQuery.java
│   │       ├── GetComponentsByTypeIdQuery.java
│   │       ├── GetComponentsByIdsQuery.java
│   │       ├── GetComponentTypeByIdQuery.java
│   │       ├── GetAllComponentTypesQuery.java
│   │       ├── GetStockItemDetailsQuery.java
│   │       └── GetAllPhotosByPropertyIdQuery.java
│   └── 📁 services/
│       ├── PropertyCommandService.java
│       ├── PropertyQueryService.java
│       ├── TechnicianInventoryCommandService.java
│       ├── TechnicianInventoryQueryService.java
│       ├── ComponentCommandService.java
│       ├── ComponentQueryService.java
│       ├── ComponentTypeCommandService.java
│       └── ComponentTypeQueryService.java
│
├── 📁 infrastructure/
│   └── 📁 persistence/jpa/repositories/
│       ├── PropertyRepository.java
│       ├── TechnicianInventoryRepository.java
│       ├── ComponentRepository.java
│       ├── ComponentStockRepository.java
│       └── ComponentTypeRepository.java
│
└── 📁 interfaces/
    ├── 📁 acl/
    │   └── InventoryContextFacade.java
    └── 📁 rest/
        ├── PropertyController.java
        ├── TechnicianInventoryController.java
        ├── ComponentController.java
        ├── ComponentTypeController.java
        ├── 📁 resource/
        │   ├── CreatePropertyResource.java
        │   ├── PropertyResource.java
        │   ├── UpdatePropertyResource.java
        │   ├── CreateTechnicianInventoryResource.java
        │   ├── TechnicianInventoryResource.java
        │   ├── CreateComponentResource.java
        │   ├── ComponentResource.java
        │   ├── UpdateComponentResource.java
        │   ├── CreateComponentTypeResource.java
        │   ├── ComponentTypeResource.java
        │   ├── AddComponentStockResource.java
        │   ├── UpdateComponentStockResource.java
        │   ├── ComponentStockResource.java
        │   ├── ComponentLookupResource.java
        │   ├── AddressResource.java
        │   ├── RegionResource.java
        │   └── DistrictResource.java
        └── 📁 transform/
            ├── CreatePropertyCommandFromResourceAssembler.java
            ├── UpdatePropertyCommandFromResourceAssembler.java
            ├── PropertyResourceFromEntityAssembler.java
            ├── CreateTechnicianInventoryCommandFromResourceAssembler.java
            ├── TechnicianInventoryResourceFromEntityAssembler.java
            ├── CreateComponentCommandFromResourceAssembler.java
            ├── ComponentResourceFromEntityAssembler.java
            ├── CreateComponentTypeCommandFromResourceAssembler.java
            ├── ComponentTypeResourceFromEntityAssembler.java
            ├── AddComponentStockCommandFromResourceAssembler.java
            ├── UpdateComponentStockCommandFromResourceAssembler.java
            ├── ComponentStockResourceFromEntityAssembler.java
            └── ComponentLookupResourceFromEntityAssembler.java
```

---

<a name="domain"></a>

## 2. Domain Layer

**Patrones aplicados (Domain):** Aggregate Root, Entity, Value Object, Domain Service (interfaces).

---

### 2.1 Aggregate Root: `Property`

**Ubicación:** `src/main/java/com/hampcoders/electrolink/assets/domain/model/aggregates/Property.java`

**Responsabilidad:** Aggregate raíz que representa una propiedad (inmueble) de un propietario. Gestiona la dirección, región, distrito y relación con el propietario.

**Invariantes principales:**

- `ownerId` identifica al propietario.
- `address` es obligatorio.
- `region` y `district` definen la ubicación geográfica.

**Fragmento de implementación:**

```java
@Entity
@Table(name = "properties")
@Getter
public class Property extends AuditableAbstractAggregateRootNoId<Property> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    private OwnerId ownerId;

    @Embedded
    private Address address;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "region_name"))
    })
    private Region region;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "district_name"))
    })
    private District district;

    public Property(final CreatePropertyCommand command) {
        this.ownerId = command.ownerId();
        this.address = command.address();
        this.region = command.region();
        this.district = command.district();
    }

    public void update(final UpdatePropertyCommand command) {
        this.address = command.address();
        this.region = command.region();
        this.district = command.district();
    }
}
```

---

### 2.2 Aggregate Root: `TechnicianInventory`

**Ubicación:** `src/main/java/com/hampcoders/electrolink/assets/domain/model/aggregates/TechnicianInventory.java`

**Responsabilidad:** Aggregate raíz que gestiona el inventario de componentes de un técnico. Gestiona la lista de stock de componentes.

**Invariantes principales:**

- `technicianId` identifica al técnico.
- `stockList` contiene la lista de componentes en inventario.
- Permite agregar, actualizar y eliminar items de stock.

**Fragmento de implementación:**

```java
@Entity
@Table(name = "technician_inventories")
@Getter
public class TechnicianInventory extends AuditableAbstractAggregateRootNoId<TechnicianInventory> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "technician_id", nullable = false)
    private Long technicianId;

    @Embedded
    private final InventoryStockList stockList;

    public TechnicianInventory(final CreateTechnicianInventoryCommand command) {
        this();
        this.technicianId = command.technicianId().technicianId();
    }

    public void addToStock(final Component component, final int quantity, final int threshold) {
        this.stockList.addItem(this, component, quantity, threshold);
    }

    public boolean updateStockItem(final UpdateComponentStockCommand command) {
        final Optional<ComponentStock> stockToUpdate = this.stockList.getItems().stream()
            .filter(stock -> stock.getComponent().getComponentUid().equals(command.componentId()))
            .findFirst();

        if (stockToUpdate.isEmpty()) {
            return false;
        }

        final ComponentStock stock = stockToUpdate.get();
        stock.updateQuantity(command.newQuantity());
        stock.updateAlertThreshold(command.newAlertThreshold());
        stock.updateLastUpdated(new Date());
        return false;
    }
}
```

---

### 2.3 Aggregate Root: `Component`

**Responsabilidad:** Catálogo de componentes disponibles en el sistema (materiales, herramientas, repuestos).

### 2.4 Aggregate Root: `ComponentType`

**Responsabilidad:** Catálogo de tipos de componentes.

---

### 2.5 Entity: `ComponentStock`

**Responsabilidad:** Representa la cantidad de un componente específico en el inventario de un técnico.

---

### 2.6 Value Objects

|Value Object|Descripción|
|:--|:--|
|`Address`|Dirección de la propiedad|
|`Region`|Región geográfica|
|`District`|Distrito/zona|
|`OwnerId`|Identificador del propietario|
|`TechnicianId`|Identificador del técnico|
|`ComponentId`|Identificador del componente|
|`ComponentTypeId`|Identificador del tipo de componente|
|`InventoryStockList`|Colección de items de stock|
|`PropertyStatuses`|Estados de propiedad|
|`PropertyPhoto`|Foto de la propiedad|

---

### 2.7 Commands (CQRS - Write)

```java
// Property commands
public record CreatePropertyCommand(OwnerId ownerId, Address address, Region region, District district) {}
public record UpdatePropertyCommand(UUID propertyId, Address address, Region region, District district) {}
public record DeletePropertyCommand(UUID propertyId) {}

// Technician Inventory commands
public record CreateTechnicianInventoryCommand(TechnicianId technicianId) {}
public record DeleteTechnicianInventoryCommand(UUID inventoryId) {}

// Component Stock commands
public record AddComponentStockCommand(UUID inventoryId, Component component, int quantity, int threshold) {}
public record UpdateComponentStockCommand(String componentId, int newQuantity, int newAlertThreshold) {}

// Component commands
public record CreateComponentCommand(String name, String description, UUID componentTypeId) {}
public record UpdateComponentCommand(UUID componentId, String name, String description) {}
public record DeleteComponentCommand(UUID componentId) {}

// Component Type commands
public record CreateComponentTypeCommand(String name, String description) {}
public record UpdateComponentTypeCommand(UUID typeId, String name, String description) {}
public record DeleteComponentTypeCommand(UUID typeId) {}
```

---

### 2.8 Queries (CQRS - Read)

```java
public record GetAllPropertiesQuery() {}
public record GetPropertyByIdQuery(UUID propertyId) {}
public record GetAllPropertiesByOwnerIdQuery(Long ownerId) {}
public record GetInventoryByTechnicianIdQuery(Long technicianId) {}
public record GetInventoriesWithLowStockQuery() {}
public record GetComponentByIdQuery(UUID componentId) {}
public record GetComponentsByNameQuery(String name) {}
public record GetComponentsByTypeIdQuery(UUID typeId) {}
public record GetComponentsByIdsQuery(List<UUID> ids) {}
public record GetComponentTypeByIdQuery(UUID typeId) {}
public record GetAllComponentTypesQuery() {}
public record GetStockItemDetailsQuery(UUID inventoryId, UUID componentId) {}
public record GetAllPhotosByPropertyIdQuery(UUID propertyId) {}
```

---

### 2.9 Domain Services

|Servicio|Responsabilidad|
|:--|:--|
|`PropertyCommandService`|Handle de commands de propiedades|
|`PropertyQueryService`|Handle de queries de propiedades|
|`TechnicianInventoryCommandService`|Handle de commands de inventario|
|`TechnicianInventoryQueryService`|Handle de queries de inventario|
|`ComponentCommandService`|Handle de commands de componentes|
|`ComponentQueryService`|Handle de queries de componentes|
|`ComponentTypeCommandService`|Handle de commands de tipos de componente|
|`ComponentTypeQueryService`|Handle de queries de tipos de componente|

---

<a name="application"></a>

## 3. Application Layer

---

### 3.1 Command Services

- `PropertyCommandServiceImpl`: Crear, actualizar, eliminar propiedades.
- `TechnicianInventoryCommandServiceImpl`: Gestionar inventario de técnicos.
- `ComponentCommandServiceImpl`: Gestionar componentes.
- `ComponentTypeCommandServiceImpl`: Gestionar tipos de componentes.

---

### 3.2 Query Services

- `PropertyQueryServiceImpl`: Consultar propiedades con filtros.
- `TechnicianInventoryQueryServiceImpl`: Consultar inventarios y stock.

---

<a name="infrastructure"></a>

## 4. Infrastructure Layer

---

### 4.1 Repositorios JPA

|Repositorio|Extiende|
|:--|:--|
|`PropertyRepository`|`JpaRepository<Property, UUID>`|
|`TechnicianInventoryRepository`|`JpaRepository<TechnicianInventory, UUID>`|
|`ComponentRepository`|`JpaRepository<Component, UUID>`|
|`ComponentStockRepository`|`JpaRepository<ComponentStock, Long>`|
|`ComponentTypeRepository`|`JpaRepository<ComponentType, UUID>`|

---

<a name="interfaces"></a>

## 5. Interfaces Layer

---

### 5.1 REST Controllers

**PropertyController** (`/api/v1/properties`)

|Método|Endpoint|Descripción|
|:--|:--|:--|
|`POST`|`/api/v1/properties`|Crear propiedad|
|`GET`|`/api/v1/properties`|Listar propiedades|
|`GET`|`/api/v1/properties/{propertyId}`|Obtener propiedad por ID|
|`GET`|`/api/v1/properties/owner/{ownerId}`|Listar propiedades por propietario|
|`PUT`|`/api/v1/properties/{propertyId}`|Actualizar propiedad|
|`DELETE`|`/api/v1/properties/{propertyId}`|Eliminar propiedad|

**TechnicianInventoryController** (`/api/v1/technician-inventories`)

|Método|Endpoint|Descripción|
|:--|:--|:--|
|`POST`|`/api/v1/technician-inventories`|Crear inventario|
|`GET`|`/api/v1/technician-inventories/{technicianId}`|Obtener inventario por técnico|
|`GET`|`/api/v1/technician-inventories/low-stock`|Listar inventarios con stock bajo|
|`POST`|`/api/v1/technician-inventories/{inventoryId}/stock`|Agregar componente al stock|
|`PATCH`|`/api/v1/technician-inventories/{inventoryId}/stock/{componentId}`|Actualizar stock|
|`DELETE`|`/api/v1/technician-inventories/{inventoryId}/stock/{componentId}`|Eliminar componente del stock|

**ComponentController** (`/api/v1/components`)

|Método|Endpoint|Descripción|
|:--|:--|:--|
|`POST`|`/api/v1/components`|Crear componente|
|`GET`|`/api/v1/components`|Listar componentes|
|`GET`|`/api/v1/components/{componentId}`|Obtener componente por ID|
|`GET`|`/api/v1/components/search`|Buscar componentes por nombre|
|`PUT`|`/api/v1/components/{componentId}`|Actualizar componente|
|`DELETE`|`/api/v1/components/{componentId}`|Eliminar componente|

**ComponentTypeController** (`/api/v1/component-types`)

|Método|Endpoint|Descripción|
|:--|:--|:--|
|`POST`|`/api/v1/component-types`|Crear tipo de componente|
|`GET`|`/api/v1/component-types`|Listar tipos de componentes|
|`GET`|`/api/v1/component-types/{typeId}`|Obtener tipo por ID|
|`PUT`|`/api/v1/component-types/{typeId}`|Actualizar tipo|
|`DELETE`|`/api/v1/component-types/{typeId}`|Eliminar tipo|

---

### 5.2 ACL (Interfaz de Fachada)

**Ubicación:** `src/main/java/com/hampcoders/electrolink/assets/interfaces/acl/InventoryContextFacade.java`

Expone métodos para consumo por otros BCs:

```java
public interface InventoryContextFacade {
    boolean hasComponentAvailable(Long technicianId, UUID componentId, int quantity);
    List<ComponentLookupResource> getAvailableComponents(Long technicianId);
    void deductStock(Long technicianId, UUID componentId, int quantity);
}
```

---

<a name="database"></a>

## 6. Esquema de Base de Datos

**Tabla:** `properties`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|UUID|PK|
|`owner_id`|BIGINT|NOT NULL|
|`address`|VARCHAR(100)|NOT NULL|
|`region_name`|VARCHAR(50)|NOT NULL|
|`district_name`|VARCHAR(50)|NOT NULL|

**Tabla:** `technician_inventories`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|UUID|PK|
|`technician_id`|BIGINT|NOT NULL|

**Tabla:** `components`

|Columna|T

ipo|Constraints|
|:--|:--|:--|
|`id`|UUID|PK|
|`component_uid`|VARCHAR(50)|NOT NULL, UNIQUE|
|`name`|VARCHAR(100)|NOT NULL|
|`description`|TEXT|NULLABLE|
|`component_type_id`|UUID|FK → component_types.id|
|`created_at`|TIMESTAMP|NOT NULL|

**Tabla:** `component_types`

|Columna|Tipo|Constraints|
|:--|:--|:--|
|`id`|UUID|PK|
|`name`|VARCHAR(50)|NOT NULL|
|`description`|TEXT|NULLABLE|

---

<a name="flujos"></a>

## 7. Flujos CQRS por Caso de Uso

### 7.1 Crear Propiedad

1. `PropertyController.createProperty` recibe `CreatePropertyResource`.
2. `CreatePropertyCommandFromResourceAssembler` crea el comando.
3. `PropertyCommandServiceImpl.handle`:
   - Crea la Property.
   - Persiste en repositorio.
4. Retorna `PropertyResource` con status 201.

### 7.2 Agregar Componente al Inventario del Técnico

1. `TechnicianInventoryController.addStock` recibe `AddComponentStockResource`.
2. `AddComponentStockCommandFromResourceAssembler` crea el comando.
3. `TechnicianInventoryCommandServiceImpl.handle`:
   - Busca el inventario.
   - Ejecuta `inventory.addToStock(component, quantity, threshold)`.
   - Persiste cambios.
4. Retorna `ComponentStockResource`.

### 7.3 Verificar Stock Disponible (desde SDP)

1. SDP BC llama `InventoryContextFacade.hasComponentAvailable(technicianId, componentId, quantity)`.
2. Busca inventario del técnico.
3. Verifica si existe el componente con stock suficiente.
4. Retorna boolean.

---

<a name="integracion"></a>

## 8. Integración con otros BCs

### 8.1 SDP (Service Design and Planning)

- **Entrada (Inbound):** SDP BC consulta `InventoryContextFacade` para verificar disponibilidad de componentes antes de asignar un técnico.
- **Salida:** SDP necesita conocer el stock disponible para validar si el técnico puede realizar el servicio.

### 8.2 Profiles BC

- **Salida (Outbound):** Profiles BC puede usar `ExternalAssetsService` para acceder a propiedades del HomeOwner y al inventario del Technician.

### 8.3 Subscription BC

- No hay integración directa.

---

**Supuestos declarados:**

1. Los IDs de Property y TechnicianInventory usan UUID en lugar de Long.
2. El inventario de componentes se usa para validar capacidad del técnico al asignar servicios.
3. Las alertas de stock bajo se disparan cuando la cantidad está por debajo del umbral definido.