package com.hampcoders.electrolink.sdp.domain.model.aggregates;

import com.hampcoders.electrolink.sdp.domain.model.entities.Bill;
import com.hampcoders.electrolink.sdp.domain.model.entities.Photo;
import com.hampcoders.electrolink.sdp.domain.model.commands.CreateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateRequestCommand;
import com.hampcoders.electrolink.sdp.domain.model.events.RequestCreatedEvent;
import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

/**
 * Represents a service request as an aggregate root.
 * This entity contains all the information related to a client's service request.
 */
@Entity
@Getter
public class Request extends AuditableAbstractAggregateRoot<Request> {

  private String clientId;
  private String technicianId;
  private String propertyId;
  private String serviceId;

  private String problemDescription;
  private LocalDate scheduledDate;
  private boolean isPriority;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "createdAt", column = @Column(name = "bill_created_at")),
      @AttributeOverride(name = "updatedAt", column = @Column(name = "bill_updated_at"))
  })
  private Bill bill;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "request_id")
  private List<Photo> photos = new ArrayList<>();

  protected Request() {
  }

  public Request(final CreateRequestCommand command) {
    this.clientId = command.clientId();
    this.technicianId = command.technicianId();
    this.propertyId = command.propertyId();
    this.serviceId = command.serviceId();
    this.problemDescription = command.problemDescription();
    this.scheduledDate = command.scheduledDate();
    this.bill = command.bill();
    this.photos = command.photos() != null ? new ArrayList<>(command.photos()) : new ArrayList<>();
    this.isPriority = command.isPriority();
  }

  public void updateFrom(final UpdateRequestCommand command) {
    this.clientId = command.clientId();
    this.technicianId = command.technicianId();
    this.propertyId = command.propertyId();
    this.serviceId = command.serviceId();
    this.problemDescription = command.problemDescription();
    this.scheduledDate = command.scheduledDate();
    this.isPriority = command.isPriority();
    this.bill = command.bill();
    this.photos.clear();
    if (command.photos() != null) {
      this.photos.addAll(command.photos());
    }
  }

  public void assignTechnician(String technicianId) {
    this.technicianId = technicianId;
  }

  public void registerCreatedEvent() {
    registerEvent(new RequestCreatedEvent(this.getId(), Long.valueOf(this.clientId),
        this.serviceId, this.isPriority, this.propertyId));
  }

  public boolean isPriority() {
    return isPriority;
  }
}
