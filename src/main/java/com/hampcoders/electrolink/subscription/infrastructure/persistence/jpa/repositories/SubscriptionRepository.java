package com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Subscription;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.SubscriptionStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Subscription entities in the database.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
  /**
   * Finds a Subscription by the associated user ID.
   *
   * @param userId The ID of the user associated with the subscription.
   * @return An Optional containing the Subscription if found, or empty if not found.
   */
  Optional<Subscription> findByUserId(Long userId);

  /**
   * Finds a Subscription by the associated user ID and subscription status.
   *
   * @param userId The ID of the user associated with the subscription.
   * @param status The status of the subscription.
   * @return An Optional containing the Subscription if found, or empty if not found.
   */
  Optional<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
}
