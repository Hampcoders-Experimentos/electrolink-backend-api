package com.hampcoders.electrolink.subscription.application.internal.commandservices;

import com.hampcoders.electrolink.subscription.domain.model.aggregates.Plan;
import com.hampcoders.electrolink.subscription.domain.model.commands.CreatePlanCommand;
import com.hampcoders.electrolink.subscription.domain.model.valueobjects.PlanType;
import com.hampcoders.electrolink.subscription.infrastructure.persistence.jpa.repositories.PlanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanCommandServiceImplTest {

    @Mock
    private PlanRepository planRepository;
    @InjectMocks
    private PlanCommandServiceImpl planCommandService;

    @Test
    @DisplayName("handle(CreatePlanCommand) should create and save plan (AAA)")
    void handleCreateCommand_ShouldCreate() {
        var command = new CreatePlanCommand(PlanType.PREMIUM, "Premium plan", 29.99, 100, true);

        when(planRepository.save(any(Plan.class))).thenAnswer(i -> i.getArgument(0));

        var result = planCommandService.handle(command);

        assertNotNull(result);
        verify(planRepository).save(any(Plan.class));
        verifyNoMoreInteractions(planRepository);
    }
}
