package com.hampcoders.electrolink.monitoring.application.internal.eventhandlers;

import com.hampcoders.electrolink.assets.interfaces.acl.InventoryContextFacade;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.ComponentStockResource;
import com.hampcoders.electrolink.monitoring.domain.model.events.ServiceCompletedEvent;
import com.hampcoders.electrolink.sdp.interfaces.acl.SdpContextFacade;
import com.hampcoders.electrolink.sdp.interfaces.acl.SdpContextFacade.ServiceComponentRequirement;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event handler that listens for ServiceCompletedEvent
 * and deducts stock of components used in the service.
 */
@Component
public class StockDeductionOnServiceCompletedHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      StockDeductionOnServiceCompletedHandler.class);

  private final SdpContextFacade sdpContextFacade;
  private final InventoryContextFacade inventoryContextFacade;

  /**
   * Constructor for StockDeductionOnServiceCompletedHandler.
   *
   * @param sdpContextFacade the facade to access SDP context information
   * @param inventoryContextFacade the facade to access inventory context information
   */
  public StockDeductionOnServiceCompletedHandler(SdpContextFacade sdpContextFacade,
                                                 InventoryContextFacade inventoryContextFacade) {
    this.sdpContextFacade = sdpContextFacade;
    this.inventoryContextFacade = inventoryContextFacade;
  }

  /**
   * Event handler method that is triggered when a ServiceCompletedEvent is published.
   *
   * @param event the ServiceCompletedEvent containing details about the completed service operation
   */
  @TransactionalEventListener
  public void onServiceCompleted(ServiceCompletedEvent event) {
    LOGGER.info("Stock deduction triggered for service operation: {}", event.serviceOperationId());

    Optional<String> serviceIdOpt = sdpContextFacade.fetchRequestServiceId(event.requestId());
    if (serviceIdOpt.isEmpty()) {
      LOGGER.warn("Request not found for ID: {}. Cannot perform stock deduction.",
          event.requestId());
      return;
    }

    String serviceId = serviceIdOpt.get();
    if (serviceId.isBlank()) {
      LOGGER.warn("Request {} has no serviceId associated. Skipping stock deduction.",
          event.requestId());
      return;
    }

    List<ServiceComponentRequirement> components =
        sdpContextFacade.fetchServiceComponentRequirements(Long.parseLong(serviceId));

    if (components == null || components.isEmpty()) {
      LOGGER.info("Service {} has no required components. No stock to deduct.", serviceId);
      return;
    }

    Long technicianId = event.technicianId();
    for (ServiceComponentRequirement componentQty : components) {
      try {
        Long componentId = componentQty.componentId();
        int quantity = componentQty.quantity();

        Optional<ComponentStockResource> stockOpt = inventoryContextFacade
            .findComponentStock(technicianId, componentId);

        if (stockOpt.isEmpty()) {
          LOGGER.warn("Component {} not found in technician {} inventory. Skipping deduction.",
              componentId, technicianId);
          continue;
        }

        ComponentStockResource stock = stockOpt.get();
        int newQuantity = stock.quantityAvailable() - quantity;
        if (newQuantity < 0) {
          LOGGER.warn("Insufficient stock for component {} in technician {} inventory. "
                  + "Available: {}, required: {}. Setting to 0.",
              componentId, technicianId, stock.quantityAvailable(), quantity);
          newQuantity = 0;
        }

        inventoryContextFacade.updateComponentStock(technicianId, componentId, newQuantity);
        LOGGER.info("Deducted "
                + "{} units of component "
                + "{} from technician "
                + "{} inventory. New quantity: {}",
            quantity, componentId, technicianId, newQuantity);

      } catch (NumberFormatException e) {
        LOGGER.warn("Invalid componentId format: {}. Skipping deduction.",
            componentQty.componentId());
      } catch (Exception e) {
        LOGGER.error("Error deducting component {} from technician {} inventory.",
            componentQty.componentId(), technicianId, e);
      }
    }
  }
}
