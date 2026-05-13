package com.hampcoders.electrolink.monitoring.domain.model.aggregates;

import com.hampcoders.electrolink.monitoring.domain.model.commands.AddRatingCommand;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import com.hampcoders.electrolink.shared.domain.model.aggregates.AuditableAbstractAggregateRootNoId;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * Represents a user rating for a completed service operation.
 * It is an Aggregate Root within the Monitoring context.
 */
@Entity
@Table
public class Rating extends AuditableAbstractAggregateRootNoId<Rating> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  @Getter
  private Long id;

  @Getter
  @Embedded
  @Column(nullable = false)
  private RequestId requestId;

  @Getter
  @NotNull
  @Min(1)
  @Max(5)
  @Column(nullable = false)
  private Integer score;

  @Getter
  @Size(max = 300)
  @Column
  private String comment;

  @Getter
  @NotBlank
  @Column(nullable = false)
  private String raterId;

  @Getter
  @Embedded
  @Column(nullable = false)
  private TechnicianId technicianId;

  @Getter
  @Column(name = "is_featured")
  private Boolean isFeatured = false;

  protected Rating() {
  }

  public Rating(AddRatingCommand command) {
    this.requestId = command.requestId();
    this.score = command.score();
    this.comment = command.comment();
    this.raterId = command.raterId();
    this.technicianId = command.technicianId();
    this.isFeatured = false;
  }

  public void updateScore(int score) {
    this.score = score;
  }

  public void updateComment(String comment) {
    this.comment = comment;
  }

  public void feature() {
    this.isFeatured = true;
  }

  public void unfeature() {
    this.isFeatured = false;
  }
}
