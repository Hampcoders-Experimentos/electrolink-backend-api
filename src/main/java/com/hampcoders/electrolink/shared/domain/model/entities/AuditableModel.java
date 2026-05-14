package com.hampcoders.electrolink.shared.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.util.Date;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Base class for entities that includes auditing fields (createdAt and updatedAt).
 * This class is intended to be extended by JPA entities that require auditing.
 */
@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class AuditableModel {

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Date createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private Date updatedAt;
}