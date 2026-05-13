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
import java.time.OffsetDateTime;
import lombok.Getter;

/**
 * Represents a service operation performed by a technician in response to a request.
 * It is an Aggregate Root in the Monitoring context.
 */
@Entity
@Table
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

  public ServiceOperation() {
  }

  public ServiceOperation(CreateServiceOperationCommand command) {
    this.requestId = command.requestId();
    this.technicianId = command.technicianId();
    this.startedAt = command.startedAt();
    this.completedAt = command.completedAt();
    this.currentStatus = command.currentStatus();
  }

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

  public ServiceStatus getStatus() {
    return currentStatus;
  }

}