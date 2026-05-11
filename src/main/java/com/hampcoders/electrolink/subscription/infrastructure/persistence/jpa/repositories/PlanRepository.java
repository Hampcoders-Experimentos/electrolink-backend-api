package com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByName(PlanType name);
    boolean existsByName(PlanType name);
}
