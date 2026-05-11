package com.hampcoders.electrolink.sdp.domain.model.aggregates;

import com.hampcoders.electrolink.sdp.domain.model.entities.ComponentQuantity;
import com.hampcoders.electrolink.sdp.domain.model.entities.Tag;
import com.hampcoders.electrolink.sdp.domain.model.events.ServiceCataloguedEvent;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Policy;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Restriction;
import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a service offered by the platform as an aggregate root.
 */
@Entity
@Getter
@NoArgsConstructor
public class ServiceEntity extends AuditableAbstractAggregateRoot<ServiceEntity> {

  private String name;
  private String description;
  private Double basePrice;
  private String estimatedTime;
  private String category;
  private boolean isVisible;
  private String createdBy;

  @Embedded
  private Policy policy;

  @Embedded
  private Restriction restriction;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "service_id")
  private List<Tag> tags = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "service_id")
  private List<ComponentQuantity> components = new ArrayList<>();

  /**
   * Constructs a new ServiceEntity.
   *
   * @param name          The name of the service.
   *
   * @param description   A description of the service.
   *
   * @param basePrice     The base price of the service.
   *
   * @param estimatedTime The estimated time to complete the service.
   *
   * @param category      The category of the service.
   *
   * @param isVisible     Whether the service is visible to clients.
   *
   * @param createdBy     The ID of the user who created the service.
   *
   * @param policy        The service's policy.
   *
   * @param restriction   The service's restrictions.
   *
   * @param tags          A list of tags associated with the service.
   *
   * @param components    A list of components required for the service.
   *
   */
  public ServiceEntity(String name, String description, Double basePrice,
                       String estimatedTime, String category,
                       boolean isVisible, String createdBy, Policy policy, Restriction restriction,
                       List<Tag> tags, List<ComponentQuantity> components) {
    this.name = name;
    this.description = description;
    this.basePrice = basePrice;
    this.estimatedTime = estimatedTime;
    this.category = category;
    this.isVisible = isVisible;
    this.createdBy = createdBy;
    this.policy = policy;
    this.restriction = restriction;
    this.tags = tags != null ? tags : new ArrayList<>();
    this.components = components != null ? components : new ArrayList<>();
  }

  /**
   * Updates the service's data from another ServiceEntity instance.
   *
   * @param updated The ServiceEntity instance with the new data.
   *
   */
  public void updateFrom(ServiceEntity updated) {
    this.name = updated.name;
    this.description = updated.description;
    this.basePrice = updated.basePrice;
    this.estimatedTime = updated.estimatedTime;
    this.category = updated.category;
    this.isVisible = updated.isVisible;
    this.policy = updated.policy;
    this.restriction = updated.restriction;
    this.tags.clear();
    this.tags.addAll(updated.tags);
    this.components.clear();
    this.components.addAll(updated.components);
  }

  public void registerCreatedEvent() {
    registerEvent(new ServiceCataloguedEvent(this.getId(), this.createdBy, this.name));
  }

}
