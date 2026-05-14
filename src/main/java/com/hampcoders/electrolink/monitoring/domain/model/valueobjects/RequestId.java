package com.hampcoders.electrolink.monitoring.domain.model.valueobjects;

import java.io.Serializable;

/**
 * Value object representing a unique identifier for a request in the monitoring system.
 *
 * @param requestId The unique identifier for the request.
 */
public record RequestId(Long requestId) implements Serializable {
}
