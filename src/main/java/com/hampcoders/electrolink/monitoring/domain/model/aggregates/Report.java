package com.hampcoders.electrolink.monitoring.domain.model.aggregates;

import com.hampcoders.electrolink.monitoring.domain.model.commands.AddReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.ReportType;
import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRootNoId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * Represents a formal report associated with a service operation.
 * It is an Aggregate Root within the Monitoring context.
 */
@Entity
@Table()
public class Report extends AuditableAbstractAggregateRootNoId<Report> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  @Getter
  private Long id;

  @Getter
  @Column(nullable = false)
  private Long serviceOperationId;

  @Getter
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportType reportType;

  @Getter
  @NotBlank
  @Column(nullable = false, length = 500)
  private String description;

  protected Report() {
  }

  public Report(AddReportCommand command) {
    this.serviceOperationId = command.serviceOperationId();
    this.reportType = command.reportType();
    this.description = command.description();
  }
}