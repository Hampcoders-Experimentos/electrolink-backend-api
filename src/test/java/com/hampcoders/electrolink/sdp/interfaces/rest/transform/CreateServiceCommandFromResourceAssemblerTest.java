package com.hampcoders.electrolink.sdp.interfaces.rest.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hampcoders.electrolink.sdp.domain.model.commands.CreateServiceCommand;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.ComponentQuantityResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.CreateServiceResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.PolicyResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.RestrictionResource;
import com.hampcoders.electrolink.sdp.interfaces.rest.resources.TagResource;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateServiceCommandFromResourceAssemblerTest {

  private static CreateServiceResource resource(String name) {
    return new CreateServiceResource(
        name, "desc", 100.0, "2h", "ELECTRICAL", true, "admin",
        new PolicyResource("cancel", "terms"),
        new RestrictionResource(List.of("D1"), List.of("SUNDAY"), false),
        List.of(new TagResource("urgent")),
        List.of(new ComponentQuantityResource("c1", 3)));
  }

  @Test
  @DisplayName("Given a resource, when assembling, then it maps fields and nested collections")
  void handle_ShouldMapAllFields_WhenResourceProvided() {
    // Act
    CreateServiceCommand command =
        CreateServiceCommandFromResourceAssembler.toCommandFromResource(resource("Install"));

    // Assert
    assertEquals("Install", command.name());
    assertEquals(100.0, command.price());
    assertEquals("cancel", command.policy().getCancellationPolicy());
    assertEquals(1, command.tags().size());
    assertEquals(1, command.components().size());
    assertEquals("c1", command.components().get(0).getComponentId());
  }

  @Test
  @DisplayName("Given a different name, when assembling, then it maps the new name")
  void handle_ShouldMapNewName_WhenDifferentResourceProvided() {
    // Act
    CreateServiceCommand command =
        CreateServiceCommandFromResourceAssembler.toCommandFromResource(resource("Repair"));

    // Assert
    assertEquals("Repair", command.name());
  }

  @Test
  @DisplayName("Given a null resource, when assembling, then it throws NullPointerException")
  void handle_ShouldThrowNullPointer_WhenResourceIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> CreateServiceCommandFromResourceAssembler.toCommandFromResource(null));
  }
}
