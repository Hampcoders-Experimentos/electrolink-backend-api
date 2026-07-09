# Electrolink Platform — Monitoring BC — Tactical Design de Pruebas Unitarias

---

## 📚 ÍNDICE

1. [Estructura del Proyecto de Pruebas](#estructura)
2. [Domain Layer Tests (Assemblers)](#domain)
3. [Application Layer Tests — Command Services](#application-command)
4. [Application Layer Tests — Query Services](#application-query)
5. [Application Layer Tests — Event Handlers](#application-events)
6. [Cobertura de Pruebas por Caso de Uso](#cobertura)
7. [Integración con otros BCs en Pruebas](#integracion)
8. [Supuestos y Convenciones](#supuestos)

---

<a name="estructura"></a>

## 1. Estructura del Proyecto de Pruebas

```
📁 src/test/java/com/hampcoders/electrolink/monitoring/
│
├── 📁 application/
│   └── 📁 internal/
│       ├── 📁 commandservices/
│       │   ├── ServiceOperationCommandServiceImplTest.java
│       │   ├── ReportCommandServiceImplTest.java
│       │   ├── ReportPhotoCommandServiceImplTest.java
│       │   └── RatingCommandServiceImplTest.java
│       ├── 📁 queryservices/
│       │   ├── ServiceOperationQueryServiceImplTest.java
│       │   ├── ReportQueryServiceImplTest.java
│       │   └── RatingQueryServiceImplTest.java
│       └── 📁 eventhandlers/
│           ├── MutualEvaluationTriggerHandlerTest.java
│           └── StockDeductionOnServiceCompletedHandlerTest.java
│
└── 📁 interfaces/
    └── 📁 rest/
        └── 📁 transform/
            ├── CreateServiceOperationCommandFromResourceAssemblerTest.java
            ├── CreateReportCommandFromResourceAssemblerTest.java
            ├── CreateReportPhotoCommandFromResourceAssemblerTest.java
            ├── CreateRatingCommandFromResourceAssemblerTest.java
            ├── UpdateServiceStatusCommandFromResourceAssemblerTest.java
            ├── UpdateRatingCommandFromResourceAssemblerTest.java
            ├── ServiceOperationResourceFromEntityAssemblerTest.java
            ├── ReportResourceFromEntityAssemblerTest.java
            ├── ReportPhotoResourceFromEntityAssemblerTest.java
            └── RatingResourceFromEntityAssemblerTest.java
```

**Total: 19 archivos de prueba**

|Categoría|Cantidad|
|:--|:--|
|Command Service Tests|4|
|Query Service Tests|3|
|Event Handler Tests|2|
|Assembler Tests (Resource→Command)|5|
|Assembler Tests (Entity→Resource)|4|
|Total|19|

---

<a name="domain"></a>

## 2. Domain Layer Tests (Assemblers)

**Patrón aplicado:** Triple A (Arrange, Act, Assert).
**Framework:** JUnit 5 + Mockito (solo en Entity→Resource).
**Nota:** Los assemblers son clases estáticas sin dependencias. Las pruebas de Resource→Command no requieren mocking; las de Entity→Resource usan `mock()` para aislar la entidad.

---

### 2.1 Assemblers: Resource → Command (5 tests)

#### `CreateServiceOperationCommandFromResourceAssemblerTest`

|Test|Escenario|Verificación|
|:--|:--|:--|
|`toCommandFromResource`|Mapeo normal con defaults|`requestId` → `new RequestId(50L)`, `technicianId` → `new TechnicianId(99L)`, `startedAt` = `OffsetDateTime.now()` (mockeado), `completedAt` = null, `currentStatus` = `ServiceStatus.IN_PROGRESS`|

**Particularidad:** Usa `MockedStatic<OffsetDateTime>` para fijar la fecha actual y verificar el default de `startedAt`.

#### `CreateReportCommandFromResourceAssemblerTest`

|Test|Escenario|Verificación|
|:--|:--|:--|
|`toCommandFromResource`|ReportType válido|`"INCIDENT"` → `ReportType.INCIDENT`, `serviceOperationId` y `description` pasan correctamente|
|`toCommandFromResource`|ReportType inválido|`"INVALID_TYPE"` → lanza `IllegalArgumentException`|

#### `CreateReportPhotoCommandFromResourceAssemblerTest`

|Test|Escenario|Verificación|
|:--|:--|:--|
|`toCommandFromResource`|Mapeo completo|`reportId`, `photoData` (byte[]), `fileName`, `contentType` se mapean correctamente. `assertArrayEquals` para el binario.|

#### `CreateRatingCommandFromResourceAssemblerTest`

|Test|Escenario|Verificación|
|:--|:--|:--|
|`toCommandFromResource`|Mapeo con VOs|`requestId` → `new RequestId(10L)`, `technicianId` → `new TechnicianId(11L)`, score, comment, raterId pasan correctamente|

#### `UpdateServiceStatusCommandFromResourceAssemblerTest`

|Test|Escenario|Verificación|
|:--|:--|:--|
|`toCommandFromResource`|Mapeo simple|`serviceOperationId` y `newStatus` se mapean directamente (sin transformación)|

#### `UpdateRatingCommandFromResourceAssemblerTest`

|Test|Escenario|Verificación|
|:--|:--|:--|
|`toCommandFromResource`|Mapeo simple|`ratingId`, `score`, `comment` se mapean directamente|

---

### 2.2 Assemblers: Entity → Resource (4 tests)

#### `ServiceOperationResourceFromEntityAssemblerTest`

|Test|Escenario|Verificación|
|:--|:--|:--|
|`toResourceFromEntity`|ServiceOperation COMPLETED|Extrae `requestId` (RequestId→Long), `technicianId` (TechnicianId→Long), `startedAt`, `completedAt`, `currentStatus` (ServiceStatus→String). Verifica cada getter con `verify(entity).getXxx()`.|

#### `ReportResourceFromEntityAssemblerTest`

|Test|Escenario|Verificación|
|:--|:--|:--|
|`toResourceFromEntity`|Report con MAINTENANCE|`id`, `serviceOperationId` (Long), `description`, `reportType` (ReportType→String.name())|

#### `ReportPhotoResourceFromEntityAssemblerTest`

|Test|Escenario|Verificación|
|:--|:--|:--|
|`toResourceFromEntity`|ReportPhoto con URL|`id`, `reportId`, `url` se mapean directamente|

#### `RatingResourceFromEntityAssemblerTest`

|Test|Escenario|Verificación|
|:--|:--|:--|
|`toResourceFromEntity`|Rating completo|Extrae `requestId` (RequestId→Long), `technicianId` (TechnicianId→Long), `score`, `comment`, `raterId`|

---

<a name="application-command"></a>

## 3. Application Layer Tests — Command Services

**Framework:** JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`).
**Mocks:** Repositorios JPA necesarios para cada servicio.
**Patrón:** `@Mock` para dependencias, `@InjectMocks` para el servicio bajo prueba.
**Verificación adicional:** `verifyNoMoreInteractions()` y `verifyNoInteractions()` para asegurar que no hay llamadas no esperadas.

---

### 3.1 `ServiceOperationCommandServiceImplTest` (3 tests)

**Mocks:** `ServiceOperationRepository`

|Test|Command|Escenario|Resultado Esperado|
|:--|:--|:--|:--|
|Create|`CreateServiceOperationCommand`|Creación normal con `PENDING`|`serviceOperationRepository.save()` llamado 1 vez. ID retornado es null (mock no setea ID)|
|Update (PENDING/IN_PROGRESS)|`UpdateServiceStatusCommand`|Actualizar a `PENDING`|`serviceOperationRepository.findById()` → mock, `updateStatus(PENDING)` invocado, `save()` llamado|
|Update (COMPLETED)|`UpdateServiceStatusCommand`|Actualizar a `COMPLETED`|`updateStatus(COMPLETED)` invocado → evento `ServiceCompletedEvent` disparado|
|Update (not found)|`UpdateServiceStatusCommand`|ServiceOperation no existe|`IllegalArgumentException` con mensaje "ServiceOperation not found". `save()` nunca se llama|

---

### 3.2 `ReportCommandServiceImplTest` (4 tests)

**Mocks:** `ReportRepository`, `ServiceOperationRepository`

|Test|Command|Escenario|Resultado Esperado|
|:--|:--|:--|:--|
|Create (exists)|`AddReportCommand`|ServiceOperation existe|`serviceOperationRepository.findById()` retorna mock, `reportRepository.save()` llamado. ID = null|
|Create (not found)|`AddReportCommand`|ServiceOperation no existe|`IllegalArgumentException`. `reportRepository.save()` nunca se llama. `verifyNoInteractions(reportRepository)`|
|Delete (found)|`DeleteReportCommand`|Report existe|`reportRepository.findById()` retorna mock, `reportRepository.delete()` llamado. Sin interacción con `serviceOperationRepository`|
|Delete (not found)|`DeleteReportCommand`|Report no existe|`IllegalArgumentException` con mensaje "Report not found". `delete()` nunca se llama|

---

### 3.3 `ReportPhotoCommandServiceImplTest` (2 tests)

**Mocks:** `ReportPhotoRepository`, `ReportRepository`, `PhotoStorageService`

|Test|Command|Escenario|Resultado Esperado|
|:--|:--|:--|:--|
|Create (exists)|`AddPhotoCommand`|Report existe|`reportRepository.findById()` retorna mock, `photoStorageService.storePhoto()` retorna URL, `reportPhotoRepository.save()` llamado|
|Create (not found)|`AddPhotoCommand`|Report no existe|`IllegalArgumentException` con mensaje "Report not found with ID: ". `reportPhotoRepository.save()` nunca se llama|

---

### 3.4 `RatingCommandServiceImplTest` (7 tests)

**Mocks:** `RatingRepository`, `ServiceOperationRepository`

#### CREATE

|Test|Command|Escenario|Resultado Esperado|
|:--|:--|:--|:--|
|Success|`AddRatingCommand`|ServiceOperation está `COMPLETED`|`serviceOperationRepository.findByRequestId()` retorna mock, `getStatus()` = `COMPLETED`, `ratingRepository.save()` llamado|
|Rejected|`AddRatingCommand`|ServiceOperation está `PENDING`|`IllegalStateException` con mensaje "Cannot add rating: associated ServiceOperation is not completed.". `save()` nunca se llama|
|Not found|`AddRatingCommand`|ServiceOperation no existe|`IllegalArgumentException` con mensaje "No ServiceOperation found for the given RequestId". `save()` nunca se llama|

#### UPDATE

|Test|Command|Escenario|Resultado Esperado|
|:--|:--|:--|:--|
|Success|`UpdateRatingCommand`|Rating existe|`ratingRepository.findById()` → mock, `updateScore()`, `updateComment()`, `save()` invocados|
|Not found|`UpdateRatingCommand`|Rating no existe|`IllegalArgumentException` con mensaje "Rating not found". `save()` nunca se llama|

#### DELETE

|Test|Command|Escenario|Resultado Esperado|
|:--|:--|:--|:--|
|Success|`DeleteRatingCommand`|Rating existe|`ratingRepository.findById()` → mock, `delete()` invocado|
|Not found|`DeleteRatingCommand`|Rating no existe|`IllegalArgumentException` con mensaje "Rating not found". `delete()` nunca se llama|

---

<a name="application-query"></a>

## 4. Application Layer Tests — Query Services

**Mocks:** Repositorios JPA correspondientes.
**Patrón:** `when(repository.findXxx()).thenReturn(...)` y verificación de lista u Optional.

---

### 4.1 `ServiceOperationQueryServiceImplTest` (3 tests)

|Test|Query|Escenario|Resultado Esperado|
|:--|:--|:--|:--|
|List all|`GetAllServiceOperationsQuery`|Repositorio retorna 2|`findAll()` retorna List de 2, `actual.size()` = 2|
|By ID|`GetServiceOperationByIdQuery`|ID = 10L existe|`findById(10L)` retorna `Optional.of(mock)`, `actual.isPresent()` = true|
|By ID (not found)|`GetServiceOperationByIdQuery`|ID = 11L no existe|`findById(11L)` retorna `Optional.empty()`, `actual.isEmpty()` = true|
|By Technician|`GetServiceOperationsByTechnicianIdQuery`|TechnicianId = 12L|`findByTechnicianId(new TechnicianId(12L))` retorna lista de 2|

---

### 4.2 `ReportQueryServiceImplTest` (3 tests)

|Test|Query|Escenario|Resultado Esperado|
|:--|:--|:--|:--|
|List all|`GetAllReportsQuery`|Repositorio retorna 2|`findAll()` retorna 2|
|By ID|`GetReportByIdQuery`|ID = 10L existe|`findById(10L)` retorna `Optional.of(mock)`|
|By ID (not found)|`GetReportByIdQuery`|ID = 11L no existe|`findById(11L)` retorna `Optional.empty()`|
|By ServiceOperation|`GetReportsByServiceOperationIdQuery`|serviceOperationId = 12L|`findByServiceOperationId(12L)` retorna 2|

---

### 4.3 `RatingQueryServiceImplTest` (4 tests)

|Test|Query|Escenario|Resultado Esperado|
|:--|:--|:--|:--|
|List all|`GetAllRatingsQuery`|Repositorio retorna 2|`findAll()` retorna 2|
|By ID|`GetRatingByIdQuery`|ID = 10L existe|`findById(10L)` retorna `Optional.of(mock)`|
|By ID (not found)|`GetRatingByIdQuery`|ID = 11L no existe|`findById(11L)` retorna `Optional.empty()`|
|By RequestId|`GetRatingsByRequestIdQuery`|requestId = 12L|`findByRequestId(new RequestId(12L))` retorna 2|
|By TechnicianId|`GetRatingsByTechnicianIdQuery`|technicianId = 13L|`findByTechnicianId(new TechnicianId(13L))` retorna 2|

---

<a name="application-events"></a>

## 5. Application Layer Tests — Event Handlers

**Framework:** JUnit 5 + Mockito.
**Nota:** Los handlers usan `@TransactionalEventListener` de Spring, pero las pruebas son unitarias puras (sin contexto Spring). Se prueba la lógica del método directamente.

---

### 5.1 `MutualEvaluationTriggerHandlerTest` (1 test)

**Mocks:** Ninguno (solo logging).

|Test|Evento|Escenario|Resultado Esperado|
|:--|:--|:--|:--|
|`onServiceCompleted`|`ServiceCompletedEvent(10L, 20L, 30L)`|Ejecución normal|`assertDoesNotThrow()` — el handler solo hace logging informativo|

---

### 5.2 `StockDeductionOnServiceCompletedHandlerTest` (3 tests)

**Mocks:** `SdpContextFacade`, `InventoryContextFacade`.

|Test|Escenario|Flujo|Resultado Esperado|
|:--|:--|:--|:--|
|**Normal deduct**|Service tiene componentes, stock suficiente|`SdpContextFacade.fetchRequestServiceId(requestId)` → `Optional.of("500")`, `fetchServiceComponentRequirements(500L)` → `[{componentId:10L, quantity:3}]`, `InventoryContextFacade.findComponentStock(200L, 10L)` → stock=10, `updateComponentStock(200L, 10L, 7)`|Stock se descuenta correctamente (10-3=7)|
|**Skip when no serviceId**|Request no tiene servicio asociado|`fetchRequestServiceId(100L)` → `Optional.empty()`|Handler retorna sin llamar a InventoryContextFacade. `verifyNoInteractions(inventoryContextFacade)`|
|**Set to zero**|Stock insuficiente|`findComponentStock(200L, 10L)` → stock=5, se necesitan 10|`updateComponentStock(200L, 10L, 0)` — stock nunca es negativo|

**Diagrama del flujo de descuento de stock:**

```
ServiceCompletedEvent
    │
    ▼
SdpContextFacade.fetchRequestServiceId(requestId) ─── Optional.empty() → SKIP
    │
    ▼ (serviceId encontrado)
SdpContextFacade.fetchServiceComponentRequirements(serviceId)
    │
    ▼ (componentes requeridos)
InventoryContextFacade.findComponentStock(technicianId, componentId) ─── Optional.empty() → SKIP (continue)
    │
    ▼ (stock encontrado)
newQuantity = stock.quantityAvailable() - quantity
    │
    ├── newQuantity >= 0 → updateComponentStock(technicianId, componentId, newQuantity)
    └── newQuantity < 0  → updateComponentStock(technicianId, componentId, 0)  ← NUNCA NEGATIVO
```

---

<a name="cobertura"></a>

## 6. Cobertura de Pruebas por Caso de Uso

### 6.1 Operaciones de Servicio (ServiceOperation)

|Caso de Uso|Test|Branches Cubiertos|
|:--|:--|:--|
|Crear ServiceOperation|`ServiceOperationCommandServiceImplTest`: Create|Creación exitosa|
|Actualizar estado a PENDING/IN_PROGRESS|`ServiceOperationCommandServiceImplTest`: Update PENDING|Status no terminal, solo cambia estado|
|Actualizar estado a COMPLETED|`ServiceOperationCommandServiceImplTest`: Update COMPLETED|Dispara `ServiceCompletedEvent`, setea `completedAt`|
|Actualizar estado (no encontrado)|`ServiceOperationCommandServiceImplTest`: not found|`IllegalArgumentException`, `save()` nunca llamado|
|Listar todos|`ServiceOperationQueryServiceImplTest`: GetAll|`findAll()` retorna lista|
|Buscar por ID|`ServiceOperationQueryServiceImplTest`: GetById|`findById()` retorna Optional (encontrado/vacío)|
|Buscar por Technician|`ServiceOperationQueryServiceImplTest`: getByTechnicianId|`findByTechnicianId()` retorna lista filtrada|
|Mapeo Resource→Command|`CreateServiceOperationCommandFromResourceAssemblerTest`|Defaults: IN_PROGRESS, `OffsetDateTime.now()`, completedAt=null|
|Mapeo Entity→Resource|`ServiceOperationResourceFromEntityAssemblerTest`|VO→Long, Status→String, fechas|

### 6.2 Reportes Técnicos (Report + ReportPhoto)

|Caso de Uso|Test|Branches Cubiertos|
|:--|:--|:--|
|Crear reporte (ServiceOp existe)|`ReportCommandServiceImplTest`: create exists|`serviceOperationRepository.findById()` → save Report|
|Crear reporte (ServiceOp no existe)|`ReportCommandServiceImplTest`: create not found|`IllegalArgumentException`, no save|
|Eliminar reporte (existe)|`ReportCommandServiceImplTest`: delete found|`findById()` → `delete()`|
|Eliminar reporte (no existe)|`ReportCommandServiceImplTest`: delete not found|`IllegalArgumentException`, no delete|
|Agregar foto a reporte (existe)|`ReportPhotoCommandServiceImplTest`: exists|`reportRepository.findById()` → `storePhoto()` → `save()`|
|Agregar foto (reporte no existe)|`ReportPhotoCommandServiceImplTest`: not found|`IllegalArgumentException`, no save|
|Mapeo Resource→Command (Report)|`CreateReportCommandFromResourceAssemblerTest`|ReportType válido. ReportType inválido → excepción|
|Mapeo Resource→Command (Photo)|`CreateReportPhotoCommandFromResourceAssemblerTest`|byte[], fileName, contentType|
|Mapeo Entity→Resource (Report)|`ReportResourceFromEntityAssemblerTest`|ReportType→String.name()|
|Mapeo Entity→Resource (ReportPhoto)|`ReportPhotoResourceFromEntityAssemblerTest`|IDs + URL|

### 6.3 Evaluaciones (Rating)

|Caso de Uso|Test|Branches Cubiertos|
|:--|:--|:--|
|Crear rating (COMPLETED)|`RatingCommandServiceImplTest`: COMPLETED|Rating creado exitosamente|
|Crear rating (no COMPLETED)|`RatingCommandServiceImplTest`: PENDING|`IllegalStateException`, rating NO creado|
|Crear rating (ServiceOp no existe)|`RatingCommandServiceImplTest`: not found|`IllegalArgumentException`, no save|
|Actualizar rating (existe)|`RatingCommandServiceImplTest`: update found|`updateScore()`, `updateComment()`, `save()`|
|Actualizar rating (no existe)|`RatingCommandServiceImplTest`: update not found|`IllegalArgumentException`, no save|
|Eliminar rating (existe)|`RatingCommandServiceImplTest`: delete found|`findById()` → `delete()`|
|Eliminar rating (no existe)|`RatingCommandServiceImplTest`: delete not found|`IllegalArgumentException`, no delete|
|Listar todos|`RatingQueryServiceImplTest`: GetAll|`findAll()`|
|Buscar por ID|`RatingQueryServiceImplTest`: GetById|`findById()` (encontrado/vacío)|
|Buscar por RequestId|`RatingQueryServiceImplTest`: getByRequestId|`findByRequestId()`|
|Buscar por TechnicianId|`RatingQueryServiceImplTest`: getByTechnicianId|`findByTechnicianId()`|
|Mapeo Resource→Command|`CreateRatingCommandFromResourceAssemblerTest`|VOs: RequestId, TechnicianId|
|Mapeo Resource→Command (Update)|`UpdateRatingCommandFromResourceAssemblerTest`|ratingId, score, comment|
|Mapeo Entity→Resource|`RatingResourceFromEntityAssemblerTest`|VO→Long, score, comment, raterId|

### 6.4 Eventos de Servicio Completado

|Caso de Uso|Test|Branches Cubiertos|
|:--|:--|:--|
|Trigger evaluación mutua|`MutualEvaluationTriggerHandlerTest`|Logging solo — no lanza excepción|
|Descuento de stock normal|`StockDeductionOnServiceCompletedHandlerTest`: normal|stock - quantity >= 0 → updateComponentStock|
|Descuento saltado (sin serviceId)|`StockDeductionOnServiceCompletedHandlerTest`: skip|`fetchRequestServiceId()` vacío → no interactúa con Inventory|
|Descuento con stock insuficiente|`StockDeductionOnServiceCompletedHandlerTest`: insufficient|stock - quantity < 0 → updateComponentStock a 0|

---

<a name="integracion"></a>

## 7. Integración con otros BCs en Pruebas

### 7.1 SDP BC (Service Design and Planning)

- **Clases mockeadas:** `SdpContextFacade` (interfaz de fachada)
- **Métodos usados en pruebas:**
  - `fetchRequestServiceId(Long requestId)` → `Optional<String>` — resuelve el ID del servicio asociado a una request
  - `fetchServiceComponentRequirements(Long serviceId)` → `List<ServiceComponentRequirement>` — obtiene componentes requeridos con cantidad
- **Propósito:** Validar que al completar un servicio, el handler consulta a SDP qué componentes se usaron

### 7.2 Assets BC (Inventory)

- **Clases mockeadas:** `InventoryContextFacade` (interfaz de fachada)
- **Métodos usados en pruebas:**
  - `findComponentStock(Long technicianId, Long componentId)` → `Optional<ComponentStockResource>` — verifica stock actual
  - `updateComponentStock(Long technicianId, Long componentId, int newQuantity)` — actualiza stock después del descuento
- **Propósito:** Validar que el descuento de stock se aplica correctamente en el inventario del técnico

### 7.3 Monitoring BC (dentro del mismo BC)

- **Repositorios mockeados:**
  - `ServiceOperationRepository` — `findById()`, `findByRequestId()`, `findByTechnicianId()`, `findAll()`, `save()`
  - `ReportRepository` — `findById()`, `findByServiceOperationId()`, `findAll()`, `save()`, `delete()`
  - `ReportPhotoRepository` — `save()`
  - `RatingRepository` — `findById()`, `findByRequestId()`, `findByTechnicianId()`, `findAll()`, `save()`, `delete()`
- **Servicios mockeados:**
  - `PhotoStorageService` — `storePhoto()` (outbound a servicio de almacenamiento externo)

### 7.4 Diagrama de Integración en Pruebas

```
                    ┌─────────────────────────────┐
                    │    MONITORING BC (Tests)    │
                    │                             │
                    │  ┌───────────────────────┐  │
                    │  │ CommandService Tests  │  │
                    │  │  • ServiceOperation   │  │
                    │  │  • Report             │  │
                    │  │  • ReportPhoto        │  │
                    │  │  • Rating             │  │
                    │  └──────────┬────────────┘  │
                    │             │                │
                    │  ┌──────────▼────────────┐  │
                    │  │  QueryService Tests   │  │
                    │  │  • ServiceOperation   │  │
                    │  │  • Report             │  │
                    │  │  • Rating             │  │
                    │  └──────────┬────────────┘  │
                    │             │                │
                    │  ┌──────────▼────────────┐  │
                    │  │  EventHandler Tests   │  │
                    │  │  • MutualEvaluation   │  │
                    │  │  • StockDeduction ────┼──┼────┐
                    │  └───────────────────────┘  │    │
                    └─────────────────────────────┘    │
                                                       │
                    ┌─────────────────────┐    ┌───────▼──────────┐
                    │  SDP BC (mock)      │    │  Assets BC (mock) │
                    │  SdpContextFacade   │    │ InventoryFacade   │
                    └─────────────────────┘    └──────────────────┘
```

---

<a name="supuestos"></a>

## 8. Supuestos y Convenciones

### 8.1 Framework y Herramientas

|Herramienta|Versión/Uso|
|:--|:--|
|JUnit|5 (Jupiter) con `@Test`, `@DisplayName`, `@ExtendWith(MockitoExtension.class)`|
|Mockito|Mockeos con `@Mock`, `@InjectMocks`, `mock()`, `when()`, `verify()`|
|Assertions|`assertEquals`, `assertNotNull`, `assertNull`, `assertTrue`, `assertFalse`, `assertThrows`, `assertDoesNotThrow`, `assertArrayEquals`|
|Verificaciones adicionales|`verifyNoMoreInteractions()`, `verifyNoInteractions()`, `verify(..., never())`, `verify(..., times(1))`|

### 8.2 Convenciones de Código

1. **Patrón Triple A (Arrange, Act, Assert):** Todos los tests siguen estrictamente este patrón con comentarios `// Arrange`, `// Act`, `// Assert`.
2. **Nombrado de tests:** `methodName_Scenario_ExpectedBehavior()` (ej. `handle_AddRatingCommand_ShouldSaveRating_WhenServiceCompleted`).
3. **`@DisplayName`:** Todos los tests incluyen descripción legible en español o inglés.
4. **ID nulo en Create:** Los tests de creación verifican que el ID retornado es `null` porque el mock de `save()` no setea el ID generado (comportamiento esperado).
5. **Excepciones:** Se verifica tanto el tipo de excepción (`IllegalArgumentException`, `IllegalStateException`) como el mensaje contenido.
6. **Sin contexto Spring:** Todas las pruebas son unitarias puras sin carga de contexto Spring Boot. No hay `@SpringBootTest`, `@WebMvcTest`, ni `@DataJpaTest`.

### 8.3 Cobertura de Branches

|Regla de Negocio|Cobertura|
|:--|:--|
|Rating solo cuando ServiceOperation está COMPLETED|✅ Branch COMPLETED (crea) + Branch PENDING (rechaza)|
|Report requiere ServiceOperation existente|✅ Branch existe (crea) + Branch no existe (excepción)|
|Stock nunca negativo|✅ Branch suficiente (resta) + Branch insuficiente (setea a 0)|
|Skip descuento si request no tiene serviceId|✅ Branch con serviceId (procesa) + Branch sin serviceId (skip)|
|ReportType válido en assembler|✅ Branch válido (mapea) + Branch inválido (excepción)|
|CRUD completo en todos los agregados|✅ Create, Read, Update, Delete para Rating y Report|

### 8.4 Limitaciones y Mejoras Futuras

1. **Sin tests de integración:** No hay pruebas que validen la interacción real con la base de datos (ej. `@DataJpaTest`).
2. **Sin tests de controladores REST:** No hay `@WebMvcTest` para los controllers (`ServiceOperationsController`, `RatingsController`, `ReportsController`, `ReportPhotoController`).
3. **Sin tests de infraestructura:** `LocalFilePhotoStorageService` y `CloudinaryPhotoStorageService` no tienen pruebas unitarias.
4. **Sin tests de eventos de dominio:** No se verifica que los eventos se publiquen realmente en el `ApplicationEventPublisher`. Las pruebas de `ServiceOperation.updateStatus(COMPLETED)` verifican el cambio de estado pero no la publicación del evento.
5. **Sin tests de Value Objects:** `RequestId`, `TechnicianId`, `ReportId`, `ServiceStatus`, `ReportType` no tienen pruebas específicas de validación.

---

**Resumen de métricas:**

|Métrica|Valor|
|:--|:--|
|Archivos de prueba|19|
|Tests totales|~32|
|Clases de servicio cubiertas|9 (5 command + 3 query + 1 handler)|
|BCs integrados vía mocks|2 (SDP, Assets)|
|Repositorios mockeados|4 (ServiceOperation, Report, ReportPhoto, Rating)|
|Fachadas mockeadas|2 (SdpContextFacade, InventoryContextFacade)|
|Branches de negocio cubiertos|~25|

---

> **Fecha de generación:** Mayo 2026
> **Proyecto:** Electrolink Platform
> **Framework de pruebas:** JUnit 5 + Mockito
> **Propósito:** Documento de diseño táctico para las pruebas unitarias del Bounded Context de Monitoring (Service Operation, Report, Rating y Event Handlers).
