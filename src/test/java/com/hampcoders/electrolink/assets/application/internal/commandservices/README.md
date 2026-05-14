# Unit Tests for Assets Command Services

Este directorio contiene las pruebas unitarias para los servicios de aplicación encargados de manejar los comandos del dominio `assets`. Todas las pruebas escritas siguen correctamente el patrón **Triple A (Arrange, Act, Assert)**.

## 1. ComponentCommandServiceImplTest
Pruebas para el servicio de componentes (`ComponentCommandServiceImpl`).

*   **`handleCreateCommand_ShouldCreateComponent_WhenNameIsUnique`**: Verifica que cuando se intenta crear un componente con un nombre que no existe, se guarda correctamente en el repositorio y se devuelve su ID.
*   **`handleCreateCommand_ShouldThrowException_WhenNameExists`**: Verifica que si se intenta crear un componente con un nombre ya registrado, se lance una excepción `IllegalStateException`.
*   **`handleUpdateCommand_ShouldUpdateComponent_WhenFound`**: Comprueba que al enviar un comando de actualización para un componente existente, se actualiza su información y se guarda el cambio.
*   **`handleDeleteCommand_ShouldReturnTrue_WhenComponentIsDeleted`**: Verifica que si el componente existe, se elimina correctamente devolviendo `true`.
*   **`handleDeleteCommand_ShouldReturnFalse_WhenComponentDoesNotExist`**: Verifica que si se intenta eliminar un componente que no existe, se devuelve `false` sin intentar eliminarlo.

## 2. ComponentTypeCommandServiceImplTest
Pruebas para el servicio de tipos de componentes (`ComponentTypeCommandServiceImpl`).

*   **`handleCreateCommand_ShouldCreate_WhenNameIsUnique`**: Verifica la creación exitosa de un tipo de componente cuando su nombre no se repite.
*   **`handleCreateCommand_ShouldThrowException_WhenNameExists`**: Verifica que se lanza una excepción al intentar registrar un tipo de componente con un nombre ya en uso.
*   **`handleUpdateCommand_ShouldUpdateComponentType_WhenFound`**: Comprueba que al enviar un comando de actualización para un tipo de componente existente, se actualiza su información y se guarda el cambio.
*   **`handleDeleteCommand_ShouldReturnTrue_WhenTypeExistsAndIsNotInUse`**: Comprueba que se puede eliminar un tipo de componente si existe y ningún componente lo está usando actualmente.
*   **`handleDeleteCommand_ShouldThrowException_WhenTypeExistsAndIsInUse`**: Verifica que no se puede eliminar un tipo de componente si hay componentes asociados a él (lanza `IllegalStateException`).
*   **`handleDeleteCommand_ShouldReturnFalse_WhenTypeDoesNotExist`**: Verifica que si se intenta eliminar un tipo de componente inexistente, la operación devuelve `false`.


## 3. TechnicianInventoryCommandServiceImplTest
Pruebas para el inventario de los técnicos (`TechnicianInventoryCommandServiceImpl`).

*   **`handleCreateCommand_ShouldCreateInventory_WhenNotExists`**: Verifica que se crea un nuevo inventario para un técnico si este aún no tiene uno.
*   **`handleCreateCommand_ShouldThrowException_WhenInventoryAlreadyExists`**: Verifica que no se puede crear un inventario si el técnico ya posee uno (lanza `IllegalStateException`).
*   **`handleAddStockCommand_ShouldAddStock_WhenFound`**: Comprueba que se puede agregar un componente al inventario de un técnico correctamente.
*   **`handleAddStockCommand_ShouldThrowException_WhenInventoryNotFound`**: Verifica que si el inventario no existe al intentar agregar stock, se lanza una excepción `EntityNotFoundException`.
*   **`handleUpdateStockCommand_ShouldUpdateStock_WhenInventoryAndStockFound`**: Verifica que si el técnico y el componente en su inventario existen, se actualiza correctamente la cantidad y el umbral de alerta.
*   **`handleUpdateStockCommand_ShouldThrowException_WhenStockItemNotFound`**: Verifica que si el componente no está en el inventario del técnico, no se puede actualizar y lanza una excepción.
*   **`handleDeleteStockCommand_ShouldReturnTrue_WhenStockItemIsRemoved`**: Comprueba que se puede eliminar correctamente un componente del inventario de un técnico.
*   **`handleDeleteStockCommand_ShouldReturnFalse_WhenInventoryNotFound`**: Verifica que al intentar eliminar un componente de un inventario inexistente, se devuelve `false`.

## 4. PropertyCommandServiceImplTest
Pruebas para el servicio de propiedades (`PropertyCommandServiceImpl`).

*   **`handleCreateCommand_ShouldCreateProperty`**: Verifica que el servicio guarde correctamente la propiedad en el repositorio cuando se le envía el comando de creación.
*   **`handleUpdateCommand_ShouldUpdateProperty_WhenFound`**: Verifica que si la propiedad existe en la base de datos, se actualizan sus datos y se guarda correctamente.
*   **`handleUpdateCommand_ShouldReturnEmpty_WhenNotFound`**: Comprueba que si se intenta actualizar una propiedad que no existe, el servicio no hace nada y devuelve un `Optional` vacío.
*   **`handleDeleteCommand_ShouldReturnTrue_WhenFound`**: Verifica que si la propiedad existe, se llama al método de guardado (para actualización de estado o lógica de negocio) y devuelve `true`.
*   **`handleDeleteCommand_ShouldReturnFalse_WhenNotFound`**: Comprueba que si la propiedad a eliminar no existe, el servicio no realiza acciones y devuelve `false`.
