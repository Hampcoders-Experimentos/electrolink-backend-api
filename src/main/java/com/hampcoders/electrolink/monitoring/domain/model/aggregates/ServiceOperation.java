package com.hampcoders.electrolink.monitoring.domain.model.aggregates;

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
import lombok.Setter;

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

  @Setter
  @Getter
  @Column
  private OffsetDateTime completedAt;

  @Getter
  @Embedded
  @Column(nullable = false)
  private TechnicianId technicianId;

  public ServiceOperation() {
    super();
  }

  /**
   * Constructs a new ServiceOperation aggregate.
   *
   * @param requestId The unique identifier for the request/operation.
   * @param technicianId The ID of the technician responsible for the operation.
   * @param startedAt The time the operation started.
   * @param completedAt The time the operation was completed (can be null).
   * @param currentStatus The current status of the operation.
   */
  public ServiceOperation(RequestId requestId, TechnicianId technicianId, OffsetDateTime startedAt, OffsetDateTime completedAt, ServiceStatus currentStatus) {
    this.requestId = requestId;
    this.technicianId = technicianId;
    this.startedAt = startedAt;
    this.completedAt = completedAt;
    this.currentStatus = currentStatus;
  }

  public void updateStatus(ServiceStatus status) {
    this.currentStatus = status;
    if (status == ServiceStatus.COMPLETED) {
      this.completedAt = OffsetDateTime.now();
      registerEvent(new ServiceCompletedEvent(this.id, this.requestId.requestId(), this.technicianId.technicianId()));
    }
  }

  public ServiceStatus getStatus() {
    return currentStatus;
  }

}