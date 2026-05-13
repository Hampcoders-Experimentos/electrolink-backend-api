package com.hampcoders.electrolink.monitoring.domain.services;

import com.hampcoders.electrolink.monitoring.domain.model.commands.AddReportCommand;
import com.hampcoders.electrolink.monitoring.domain.model.commands.DeleteReportCommand;

public interface ReportCommandService {

  Long handle(AddReportCommand command);

  void handle(DeleteReportCommand command);
}