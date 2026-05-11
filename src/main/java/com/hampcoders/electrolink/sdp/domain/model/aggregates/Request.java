package com.hampcoders.electrolink.sdp.domain.model.aggregates;

import com.hampcoders.electrolink.sdp.domain.model.entities.Bill;
import com.hampcoders.electrolink.sdp.domain.model.entities.Photo;
import com.hampcoders.electrolink.sdp.domain.model.events.RequestCreatedEvent;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateRequestResource;
import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a service request as an aggregate root.
 * This entity contains all the information related to a client's service request.
 */
@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

  /**
   * Constructs a new Request.
   *
   * @param clientId           The ID of the client making the request.
   *
   * @param technicianId       The ID of the technician assigned to the request.
   *
   * @param propertyId         The ID of the property where the service is requested.
   *
   * @param serviceId          The ID of the service being requested.
   *
   * @param problemDescription A description of the problem.
   *
   * @param scheduledDate      The date the service is scheduled for.
   *
   * @param bill               The billing information associated with the request.
   *
   * @param photos             A list of photos related to the request.
   *
   */
  public Request(String clientId, String technicianId, String propertyId, String serviceId,
                 String problemDescription,
                 LocalDate scheduledDate,
                 Bill bill,
                 List<Photo> photos) {
    this(clientId, technicianId, propertyId, serviceId, problemDescription, scheduledDate, bill, photos, false);
  }

  public Request(String clientId, String technicianId, String propertyId, String serviceId,
                 String problemDescription,
                 LocalDate scheduledDate,
                 Bill bill,
                 List<Photo> photos,
                 boolean isPriority) {
    this.clientId = clientId;
    this.technicianId = technicianId;
    this.propertyId = propertyId;
    this.serviceId = serviceId;
    this.problemDescription = problemDescription;
    this.scheduledDate = scheduledDate;
    this.bill = bill;
    this.photos = photos != null ? photos : new ArrayList<>();
    this.isPriority = isPriority;
  }

  /**
   * Sets the ID of the request.
   *
   * @param id The new ID for the request.
   *
   */
  public void setId(Long id) {
    super.setId(id);
  }

  /**
   * Updates the request's data from a resource object.
   *
   * @param resource The resource containing the new data.
   *
   */
  public void updateFrom(CreateRequestResource resource) {
    this.clientId = resource.clientId();
    this.technicianId = resource.technicianId();
    this.propertyId = resource.propertyId();
    this.serviceId = resource.serviceId();
    this.problemDescription = resource.problemDescription();
    this.scheduledDate = resource.scheduledDate();
    this.isPriority = resource.isPriority();

    this.bill = new Bill(
        resource.bill().billingPeriod(),
        resource.bill().energyConsumed(),
        resource.bill().amountPaid(),
        resource.bill().billImageUrl()
    );

    this.photos.clear();
    if (resource.photos() != null) {
      resource.photos().forEach(photoResource -> {
        this.photos.add(new Photo(photoResource.photoId(), photoResource.url()));
      });
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
