package com.hampcoders.electrolink.shared.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.Date;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Abstract base class for aggregate roots that includes auditing fields (createdAt and updatedAt).
 *
 * @param <T> the type of the aggregate root that extends this class
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class AuditableAbstractAggregateRoot
    <T extends AbstractAggregateRoot<T>> extends AbstractAggregateRoot<T> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Date createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private Date updatedAt;

  protected void setId(Long id) {
    this.id = id;
  }
}