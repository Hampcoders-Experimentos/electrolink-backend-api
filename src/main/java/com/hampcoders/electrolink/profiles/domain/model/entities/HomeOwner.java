package com.hampcoders.electrolink.profiles.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * Entity representing a homeowner in the system, containing
 * additional information about the homeowner.
 */
@Getter
@Entity
@Table(name = "home_owners")
public class HomeOwner {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "additional_info", length = 100)
  private String additionalInfo;

  /**
   * Default constructor for JPA.
   */
  protected HomeOwner() {}

  /**
   * Constructor to create a new HomeOwner with the specified additional information.
   *
   * @param additionalInfo Additional information about the homeowner,
   *     such as their preferences or characteristics.
   */
  public HomeOwner(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  /**
   * Method to update the additional information of the homeowner.
   *
   * @param newInfo The new additional information to be set for the homeowner.
   */
  public void updateInfo(String newInfo) {
    this.additionalInfo = newInfo;
  }
}
