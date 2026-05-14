package com.hampcoders.electrolink.profiles.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * Represents a technician in the system,
 * responsible for performing repairs and maintenance.
 */
@Getter
@Entity
@Table(name = "technicians")
public class Technician {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "certification_code", length = 50)
  private String certificationCode;

  @Column(name = "is_verified")
  private Boolean isVerified = false;

  /**
   * Default constructor for JPA.
   */
  protected Technician() {}

  /**
   * Constructs a new Technician with the given certification code.
   *
   * @param certificationCode the certification code for the technician
   */
  public Technician(String certificationCode) {
    this.certificationCode = certificationCode;
    this.isVerified = true;
  }

  /**
   * Updates the technician's certification code.
   *
   * @param newCode the new certification code
   */
  public void updateCertification(String newCode) {
    this.certificationCode = newCode;
  }

  /**
   * Marks the technician as verified, allowing them to perform repairs and maintenance.
   */
  public void verify() {
    this.isVerified = true;
  }
}
