package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.sdp.domain.model.commands.UpdateServiceCommand;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.ComponentQuantityResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateServiceResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.PolicyResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.RestrictionResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.TagResource;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateServiceCommandFromResourceAssemblerTest {

  private static CreateServiceResource resource(String name) {
    return new CreateServiceResource(
        name, "desc", 100.0, "2h", "ELECTRICAL", true, "admin",
        new PolicyResource("cancel", "terms"),
        new RestrictionResource(List.of("D1"), List.of("SUNDAY"), false),
        List.of(new TagResource("urgent")),
        List.of(new ComponentQuantityResource("c1", 3)));
  }

  @Test
  @DisplayName("Given a resource and id, when assembling, then it maps fields and nested collections")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Act
    UpdateServiceCommand command =
        UpdateServiceCommandFromResourceAssembler.toCommandFromResource(5L, resource("Install"));

    // Assert
    assertEquals(5L, command.serviceId());
    assertEquals("Install", command.name());
    assertEquals("cancel", command.policy().getCancellationPolicy());
    assertEquals(1, command.components().size());
    assertEquals("c1", command.components().get(0).getComponentId());
  }

  @Test
  @DisplayName("Given a different id, when assembling, then it maps the new id")
  void handle_ShouldMapNewId_WhenDifferentIdProvided() {
    // Act
    UpdateServiceCommand command =
        UpdateServiceCommandFromResourceAssembler.toCommandFromResource(9L, resource("Repair"));

    // Assert
    assertEquals(9L, command.serviceId());
    assertEquals("Repair", command.name());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> UpdateServiceCommandFromResourceAssembler.toCommandFromResource(5L, null));
  }
}
