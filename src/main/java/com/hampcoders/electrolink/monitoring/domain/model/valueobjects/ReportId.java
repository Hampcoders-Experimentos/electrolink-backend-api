package com.hampcoders.electrolink.monitoring.domain.model.valueobjects;

import java.io.Serializable;

/**
 * Value Object representing the unique identifier for a Report in the monitoring system.
 *
 * @param value The unique identifier value for the Report.
 */
public record ReportId(Long value) implements Serializable {
}
