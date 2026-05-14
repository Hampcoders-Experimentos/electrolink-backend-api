package com.hampcoders.electrolink.assets.infrastructure.persistence.jpa.repositories;

import com.hampcoders.electrolink.assets.domain.model.aggregates.TechnicianInventory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing TechnicianInventory aggregates.
 */
@Repository
public interface TechnicianInventoryRepository extends JpaRepository<TechnicianInventory, UUID> {

  /**
   * Finds a TechnicianInventory by its technician ID, eagerly fetching its component stocks.
   *
   * @param technicianId The ID of the technician.
   * @return An Optional containing the TechnicianInventory with its stocks.
   */
  @Query(
      "SELECT ti FROM TechnicianInventory ti LEFT JOIN FETCH ti.stockList.items "
          + "WHERE ti.technicianId = :technicianId"
  )
  Optional<TechnicianInventory> findByTechnicianIdWithStocks(@Param("technicianId")
                                                             Long technicianId);

  /**
   * Finds a TechnicianInventory by its technician ID without eagerly fetching stocks.
   *
   * @param technicianId The ID of the technician.
   * @return An Optional containing the TechnicianInventory.
   */
  Optional<TechnicianInventory> findByTechnicianId(Long technicianId);

  /**
   * Finds all TechnicianInventories that have at least
   * one stock item below the specified threshold.
   *
   * @param threshold The low stock quantity threshold.
   * @return A list of inventories with low stock items.
   */
  @Query(
      "SELECT DISTINCT i FROM TechnicianInventory i JOIN i.stockList.items s "
          + "WHERE s.quantityAvailable < :threshold"
  )
  List<TechnicianInventory> findInventoriesWithLowStock(@Param("threshold") int threshold);

  /**
   * Checks if an inventory already exists for the given technician ID.
   *
   * @param technicianId The ID of the technician.
   * @return True if an inventory exists, false otherwise.
   */
  boolean existsByTechnicianId(Long technicianId);
}