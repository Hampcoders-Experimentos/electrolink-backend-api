package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hampcoders.electrolink.sdp.domain.model.aggregates.ServiceEntity;
import com.hampcoders.electrolink.sdp.domain.model.entities.ComponentQuantity;
import com.hampcoders.electrolink.sdp.domain.model.entities.Tag;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Policy;
import com.hampcoders.electrolink.sdp.domain.model.valueobjects.Restriction;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.ServiceResource;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServiceResourceFromEntityAssemblerTest {

  @Test
  @DisplayName("Given a complete service, when assembling, then it maps fields and nested collections")
  void handle_ShouldMapAllFields_WhenServiceIsComplete() {
    // Arrange
    Policy policy = mock(Policy.class);
    when(policy.getCancellationPolicy()).thenReturn("cancel");
    when(policy.getTermsAndConditions()).thenReturn("terms");
    Restriction restriction = mock(Restriction.class);
    when(restriction.getUnavailableDistricts()).thenReturn(List.of("D1"));
    when(restriction.getForbiddenDays()).thenReturn(List.of("SUNDAY"));
    when(restriction.isRequiresSpecialCertification()).thenReturn(true);
    Tag tag = mock(Tag.class);
    when(tag.getName()).thenReturn("urgent");
    ComponentQuantity component = mock(ComponentQuantity.class);
    when(component.getComponentId()).thenReturn("c1");
    when(component.getQuantity()).thenReturn(3);
    ServiceEntity service = mock(ServiceEntity.class);
    when(service.getId()).thenReturn(3L);
    when(service.getName()).thenReturn("Install");
    when(service.getDescription()).thenReturn("desc");
    when(service.getBasePrice()).thenReturn(100.0);
    when(service.getEstimatedTime()).thenReturn("2h");
    when(service.getCategory()).thenReturn("ELECTRICAL");
    when(service.isVisible()).thenReturn(true);
    when(service.getCreatedBy()).thenReturn("admin");
    when(service.getPolicy()).thenReturn(policy);
    when(service.getRestriction()).thenReturn(restriction);
    when(service.getTags()).thenReturn(List.of(tag));
    when(service.getComponents()).thenReturn(List.of(component));

    // Act
    ServiceResource resource = ServiceResourceFromEntityAssembler.toResourceFromEntity(service);

    // Assert
    assertEquals(3L, resource.id());
    assertEquals("Install", resource.name());
    assertEquals("cancel", resource.policy().cancellationPolicy());
    assertEquals(1, resource.tags().size());
    assertEquals("c1", resource.components().get(0).componentId());
  }

  @Test
  @DisplayName("Given a service with null nested data, when assembling, then it applies defaults")
  void handle_ShouldApplyDefaults_WhenNestedDataIsNull() {
    // Arrange
    ServiceEntity service = mock(ServiceEntity.class);
    when(service.getId()).thenReturn(4L);
    when(service.getName()).thenReturn("Repair");
    when(service.getDescription()).thenReturn("desc");
    when(service.getBasePrice()).thenReturn(50.0);
    when(service.getEstimatedTime()).thenReturn("1h");
    when(service.getCategory()).thenReturn("ELECTRICAL");
    when(service.isVisible()).thenReturn(false);
    when(service.getCreatedBy()).thenReturn("admin");
    when(service.getPolicy()).thenReturn(null);
    when(service.getRestriction()).thenReturn(null);
    when(service.getTags()).thenReturn(null);
    when(service.getComponents()).thenReturn(null);

    // Act
    ServiceResource resource = ServiceResourceFromEntityAssembler.toResourceFromEntity(service);

    // Assert
    assertEquals("", resource.policy().cancellationPolicy());
    assertTrue(resource.tags().isEmpty());
    assertTrue(resource.components().isEmpty());
  }

  @Test
  @DisplayName("Given a null service, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenServiceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> ServiceResourceFromEntityAssembler.toResourceFromEntity(null));
  }
}
