package com.hampcoders.electrolink.monitoring.domain.services;

import com.hampcoders.electrolink.monitoring.domain.model.commands.AddReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.DeleteReportCommand;

/**
 * Service interface for handling report-related commands, such as adding and deleting reports.
 */
public interface ReportCommandService {

  /**
   * Handles the addition of a new report based on the provided command. This method processes the
   *
   * @param command the command containing the details of the report to be added
   * @return the ID of the newly created report
   */
  Long handle(AddReportCommand command);

  /**
   * Handles the deletion of an existing report based on the provided command. This method processes
   *
   * @param command the command containing the ID of the report to be deleted
   */
  void handle(DeleteReportCommand command);
}