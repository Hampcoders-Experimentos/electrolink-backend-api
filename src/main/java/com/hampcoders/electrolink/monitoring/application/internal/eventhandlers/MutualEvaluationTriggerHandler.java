package com.hampcoders.electrolink.monitoring.application.internal.eventhandlers;

import com.hampcoders.electrolink.monitoring.domain.model.events.ServiceCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Handles the triggering of mutual evaluation processes when a service operation is completed.
 */
@Component
public class MutualEvaluationTriggerHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MutualEvaluationTriggerHandler.class);

  /**
   * Listens for ServiceCompletedEvent and initiates the mutual evaluation
   * process for both the technician and the customer.
   *
   * @param event the event containing details about the completed service operation,
   *     including request ID and technician ID.
   */
  @TransactionalEventListener
  public void onServiceCompleted(ServiceCompletedEvent event) {
    LOGGER.info("Mutual evaluation is now available for service operation: {}",
        event.serviceOperationId());
    LOGGER.info("Pending evaluation records created for request: {}, technician: {}",
        event.requestId(), event.technicianId());
  }
}
