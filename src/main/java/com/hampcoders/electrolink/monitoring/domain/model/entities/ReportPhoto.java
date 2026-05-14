package com.hampcoders.electrolink.monitoring.domain.model.entities;

import com.hampcoders.electrolink.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * Represents a photo associated with a report in the monitoring system.
 */
@Entity
@Table
public class ReportPhoto extends AuditableModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  @Getter
  private Long id;

  @Getter
  @Column(nullable = false)
  private Long reportId;

  @Getter
  @Column(nullable = false)
  private String url;

  /**
   * Constructs a new ReportPhoto with the specified report ID and URL.
   *
   * @param reportId the ID of the report this photo is associated with
   * @param url the URL of the photo
   */
  public ReportPhoto(Long reportId, String url) {
    this.reportId = reportId;
    this.url = url;
  }

  /**
   * Protected no-argument constructor for JPA.
   */
  protected ReportPhoto() {
  }
}
