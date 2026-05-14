package com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories;

import com.hampcoders.electrolink.subscription.domain.model.entities.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing PaymentTransaction entities.
 */
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
}
