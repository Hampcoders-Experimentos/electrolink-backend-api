package com.hampcoders.electrolink.subscription.interfaces.rest;

import com.hampcoders.electrolink.subscription.domain.model.queries.GetAllPlansQuery;
import com.hampcoders.electrolink.subscription.domain.model.queries.GetPlanByIdQuery;
import com.hampcoders.electrolink.subscription.domain.services.PlanCommandService;
import com.hampcoders.electrolink.subscription.domain.services.PlanQueryService;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.CreatePlanResource;
import com.hampcoders.electrolink.subscription.interfaces.rest.resources.PlanResource;
import com.hampcoders.electrolink.subscription.interfaces.rest.transform.CreatePlanCommandFromResourceAssembler;
import com.hampcoders.electrolink.subscription.interfaces.rest.transform.PlanResourceFromEntityAssembler;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/plans")
public class PlansController {

    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

    public PlansController(PlanCommandService planCommandService,
                           PlanQueryService planQueryService) {
        this.planCommandService = planCommandService;
        this.planQueryService = planQueryService;
    }

    @GetMapping
    public ResponseEntity<List<PlanResource>> getAllPlans() {
        var plans = planQueryService.handle(new GetAllPlansQuery());
        var resources = plans.stream()
            .map(PlanResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<PlanResource> getPlanById(@PathVariable Long planId) {
        return planQueryService.handle(new GetPlanByIdQuery(planId))
            .map(plan -> ResponseEntity.ok(PlanResourceFromEntityAssembler.toResourceFromEntity(plan)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlanResource> createPlan(@RequestBody CreatePlanResource resource) {
        var command = CreatePlanCommandFromResourceAssembler.toCommandFromResource(resource);
        var plan = planCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PlanResourceFromEntityAssembler.toResourceFromEntity(plan));
    }
}
