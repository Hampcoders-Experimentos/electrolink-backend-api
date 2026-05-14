package com.hampcoders.electrolink.monitoring.domain.model.valueobjects;

import java.io.Serializable;

/**
 * Value Object representing the unique identifier of a Technician in the system.
 *
 * @param technicianId The unique identifier for the Technician.
 */
public record TechnicianId(Long technicianId) implements Serializable {
}
