package com.hampcoders.electrolink.monitoring.domain.model.valueobjects;

/**
 * Enum representing the different types of reports in the system.
 *
 * <p>Possible values are:
 * <ul>
 *   <li>INCIDENT: A report of an incident.</li>
 *   <li>COMPLETION: A report indicating the completion of an operation.</li>
 *   <li>MAINTENANCE: A report related to maintenance activities.</li>
 *   <li>OBSERVATION: A general observation report.</li>
 * </ul>
 * </p>
 */
public enum ReportType {
  INCIDENT,
  COMPLETION,
  MAINTENANCE,
  OBSERVATION
}
