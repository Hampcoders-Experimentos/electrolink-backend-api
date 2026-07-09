package com.hampcoders.electrolink.monitoring.infrastructure.persistence.jpa.repositories;

import com.hampcoders.electrolink.monitoring.domain.model.aggregates.ServiceOperation;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.RequestId;
import com.hampcoders.electrolink.monitoring.domain.model.valueobjects.TechnicianId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing ServiceOperation entities.
 * Extends JpaRepository to provide CRUD operations and additional query methods.
 */
public interface ServiceOperationRepository extends JpaRepository<ServiceOperation, Long> {

  /**
   * Finds a service operation by its specific id.
   *
   * @param id The Service Operation id to search for.
   * @return An Optional containing the found ServiceOperation, or empty if not found.
   */
  Optional<ServiceOperation> findById(Long id);

  /**
   * Finds every service operation registered for a given RequestId.
   *
   * <p>A request may have more than one service operation associated with it, so this
   * returns a list rather than an {@link Optional}. Returning an {@code Optional} here
   * would throw {@code NonUniqueResultException} as soon as duplicates exist.</p>
   *
   * @param requestId The RequestId to search for.
   * @return The list of matching service operations (empty if none).
   */
  List<ServiceOperation> findByRequestId(RequestId requestId);

  /**
   * Finds all service operations assigned to a specific technician.
   *
   * @param technicianId The TechnicianId to filter by.
   * @return A list of service operations associated with the technician.
   */
  List<ServiceOperation> findByTechnicianId(TechnicianId technicianId);
}