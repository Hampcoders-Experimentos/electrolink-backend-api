package com.hampcoders.electrolink.profiles.domain.model.aggregates;

import com.hampcoders.electrolink.profiles.domain.model.entities.HomeOwner;
import com.hampcoders.electrolink.profiles.domain.model.entities.Technician;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.EmailAddress;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.PersonName;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;
import com.hampcoders.electrolink.profiles.domain.model.valueobjects.StreetAddress;
import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * The Profile aggregate root represents a user profile in the system,
 * which can be either a HomeOwner or a Technician.
 */
@Getter
@Entity
@Table(name = "profiles")
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "firstName",
        column = @Column(name = "first_name", length = 30, nullable = false)),
    @AttributeOverride(name = "lastName",
        column = @Column(name = "last_name", length = 30, nullable = false))
  })
  private PersonName personName;

  @Embedded
  @AttributeOverride(name = "address",
      column = @Column(name = "street_address", length = 100, nullable = false))
  private StreetAddress address;

  @Embedded
  @AttributeOverride(name = "address",
      column = @Column(name = "email", length = 250, nullable = false, unique = true))
  private EmailAddress email;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "home_owner_id")
  private HomeOwner homeOwner;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "technician_id")
  private Technician technician;

  /**
   * Default constructor for JPA.
   */
  protected Profile() {}

  /**
   * Constructor to create a new Profile with the specified information.
   *
   * @param personName The person's name.
   * @param email The person's email address.
   * @param address The person's street address.
   * @param role The role of the profile.
   */
  public Profile(PersonName personName, EmailAddress email, StreetAddress address, Role role) {
    this.personName = personName;
    this.email = email;
    this.address = address;
    this.role = role;
  }

  /**
   * Assigns a HomeOwner entity to this profile.
   * This method should only be called if the role is HOMEO.
   *
   * @param homeOwner The HomeOwner entity to assign.
   */
  public void assignHomeOwner(HomeOwner homeOwner) {
    if (role != Role.HOMEOWNER) {
      throw new IllegalStateException("Cannot assign HomeOwner. Role must be HOMEOWNER.");
    }
    this.homeOwner = homeOwner;
  }

  /**
   * Assigns a Technician entity to this profile.
   *
   * @param technician The Technician entity to assign.
   */
  public void assignTechnician(Technician technician) {
    if (role != Role.TECHNICIAN) {
      throw new IllegalStateException("Cannot assign Technician. Role must be TECHNICIAN.");
    }
    this.technician = technician;
  }

  /**
   * Updates the profile information.
   * This method can be used to update the person's name, email, address, and role.
   *
   * @param personName The new person's name.
   * @param email The new email address.
   * @param address The new street address.
   * @param role The new role of the profile.
   */
  public void updateInformation(PersonName personName, EmailAddress email,
                                StreetAddress address, Role role) {
    this.personName = personName;
    this.email = email;
    this.address = address;
    this.role = role;
  }

}
