package com.hampcoders.electrolink.sdp.domain.model.aggregates;

import com.hampcoders.electrolink.sdp.domain.model.commands.CreateServiceCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateServiceCommand;
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

/**
 * Represents a service offered by the platform as an aggregate root.
 */
@Entity
@Getter
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

  protected ServiceEntity() {
  }

  public ServiceEntity(final CreateServiceCommand command) {
    this.name = command.name();
    this.description = command.description();
    this.basePrice = command.price();
    this.estimatedTime = command.estimatedTime();
    this.category = command.category();
    this.isVisible = command.isVisible();
    this.createdBy = command.createdBy();
    this.policy = command.policy();
    this.restriction = command.restriction();
    this.tags = command.tags() != null ? new ArrayList<>(command.tags()) : new ArrayList<>();
    this.components = command.components() != null ? new ArrayList<>(command.components()) : new ArrayList<>();
  }

  public void updateFrom(final UpdateServiceCommand command) {
    this.name = command.name();
    this.description = command.description();
    this.basePrice = command.price();
    this.estimatedTime = command.estimatedTime();
    this.category = command.category();
    this.isVisible = command.isVisible();
    this.createdBy = command.createdBy();
    this.policy = command.policy();
    this.restriction = command.restriction();
    this.tags.clear();
    if (command.tags() != null) {
      this.tags.addAll(command.tags());
    }
    this.components.clear();
    if (command.components() != null) {
      this.components.addAll(command.components());
    }
  }

  public void registerCreatedEvent() {
    registerEvent(new ServiceCataloguedEvent(this.getId(), this.createdBy, this.name));
  }

}
