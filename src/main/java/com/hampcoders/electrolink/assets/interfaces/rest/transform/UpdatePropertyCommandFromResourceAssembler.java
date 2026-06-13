package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.commands.UpdatePropertyCommand;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.Address;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.District;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.Region;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.UpdatePropertyResource;
import java.util.UUID;

/**
 * Assembler to convert UpdatePropertyResource into an UpdatePropertyCommand.
 */
public class UpdatePropertyCommandFromResourceAssembler {
  /**
   * Converts the resource and property ID into an UpdatePropertyCommand.
   *
   * @param propertyId The ID of the property to update.
   * @param resource The UpdatePropertyResource containing update details.
   * @return The resulting UpdatePropertyCommand.
   */
  public static UpdatePropertyCommand toCommandFromResource(
      final UUID propertyId, final UpdatePropertyResource resource) {
    var address = new Address(
        resource.address().street(),
        resource.address().number(),
        resource.address().city(),
        resource.address().postalCode(),
        resource.address().country(),
        resource.address().latitude(),
        resource.address().longitude()
    );

    var region = new Region(resource.region());
    var district = new District(resource.district());

    return new UpdatePropertyCommand(propertyId, address, region, district);
  }

  private UpdatePropertyCommandFromResourceAssembler() {
  }
}