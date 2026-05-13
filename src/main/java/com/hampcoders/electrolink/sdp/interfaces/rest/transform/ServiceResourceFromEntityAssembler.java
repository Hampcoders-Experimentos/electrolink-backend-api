package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.ComponentQuantityResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.PolicyResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.RestrictionResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.ServiceResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resource.TagResource;
import java.util.Collections;
import java.util.List;

public class ServiceResourceFromEntityAssembler {
  public static ServiceResource toResourceFromEntity(final ServiceEntity entity) {
    var policy = entity.getPolicy() != null
        ? new PolicyResource(entity.getPolicy().getCancellationPolicy(), entity.getPolicy().getTermsAndConditions())
        : new PolicyResource("", "");
    var restriction = entity.getRestriction() != null
        ? new RestrictionResource(
            entity.getRestriction().getUnavailableDistricts(),
            entity.getRestriction().getForbiddenDays(),
            entity.getRestriction().isRequiresSpecialCertification())
        : new RestrictionResource(List.of(), List.of(), false);
    var tags = entity.getTags() != null
        ? entity.getTags().stream().map(t -> new TagResource(t.getName())).toList()
        : Collections.<TagResource>emptyList();
    var components = entity.getComponents() != null
        ? entity.getComponents().stream().map(c -> new ComponentQuantityResource(c.getComponentId(), c.getQuantity())).toList()
        : Collections.<ComponentQuantityResource>emptyList();
    return new ServiceResource(
        entity.getId(), entity.getName(), entity.getDescription(),
        entity.getBasePrice(), entity.getEstimatedTime(), entity.getCategory(),
        entity.isVisible(), entity.getCreatedBy(), policy, restriction, tags, components);
  }
}
