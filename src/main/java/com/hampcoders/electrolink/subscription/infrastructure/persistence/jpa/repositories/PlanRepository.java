package com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Plan entities in the database.
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

  /**
   * Finds a Plan by its name.
   *
   * @param name the name of the Plan to find
   * @return an Optional containing the found Plan,
   *     or empty if no Plan with the given name exists
   */
  Optional<Plan> findByName(PlanType name);

  /**
   * Checks if a Plan with the given name exists in the database.
   *
   * @param name the name of the Plan to check
   * @return true if a Plan with the given name exists, false otherwise
   */
  boolean existsByName(PlanType name);
}
