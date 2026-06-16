package com.hampcoders.electrolink.monitoring.domain.model.aggregates;

import com.hampcoders.electrolink.monitoring.domain.model.commands.CreateServiceOperationCommand;
import com.hampcoders.electrolink.monitoring.domain.model.events.ServiceCompletedEvent;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ServiceStatus;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRootNoId;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import lombok.Getter;

/**
 * Represents a service operation performed by a technician in response to a request.
 * It is an Aggregate Root in the Monitoring context.
 *
 * <p>A request maps to at most one service operation: {@code request_id} carries a unique
 * constraint so the lookup used when rating stays unambiguous.</p>
 */
@Entity
@Table(uniqueConstraints =
    @UniqueConstraint(name = "uq_service_operation_request_id", columnNames = "request_id"))
public class ServiceOperation extends AuditableAbstractAggregateRootNoId<ServiceOperation> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  @Getter
  private Long id;

  @Getter
  @Embedded
  @Column(nullable = false)
  private RequestId requestId;

  @Getter
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ServiceStatus currentStatus;

  @Getter
  @Column(nullable = false)
  private OffsetDateTime startedAt;

  @Getter
  @Column
  private OffsetDateTime completedAt;

  @Getter
  @Embedded
  @Column(nullable = false)
  private TechnicianId technicianId;

  /**
   * Default constructor for JPA. Not intended for direct use.
   */
  public ServiceOperation() {
  }

  /**
   * Constructs a new ServiceOperation based on the provided command.
   *
   * @param command the command containing the details to create a new service operation
   */
  public ServiceOperation(CreateServiceOperationCommand command) {
    this.requestId = command.requestId();
    this.technicianId = command.technicianId();
    this.startedAt = command.startedAt();
    this.completedAt = command.completedAt();
    this.currentStatus = command.currentStatus();
  }

  /**
   * Updates the status of the service operation.
   *
   * @param status the new status to set for the service operation
   */
  public void updateStatus(ServiceStatus status) {
    this.currentStatus = status;
    if (status == ServiceStatus.COMPLETED) {
      this.completedAt = OffsetDateTime.now();
      registerEvent(new ServiceCompletedEvent(
          this.id,
          this.requestId.requestId(),
          this.technicianId.technicianId()
      ));
    }
  }

  /**
   * Retrieves the current status of the service operation.
   *
   * @return the current status of the service operation
   */
  public ServiceStatus getStatus() {
    return currentStatus;
  }

}