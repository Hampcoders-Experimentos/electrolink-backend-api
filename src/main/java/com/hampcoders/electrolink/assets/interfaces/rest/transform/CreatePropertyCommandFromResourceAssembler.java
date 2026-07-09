package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.commands.CreatePropertyCommand;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.Address;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.District;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.OwnerId;
import com.hampcoders.electrolink.assets.domain.model.valueobjects.Region;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.CreatePropertyResource;

/**
 * Assembler to convert CreatePropertyResource into a CreatePropertyCommand.
 */
public class CreatePropertyCommandFromResourceAssembler {
  /**
   * Converts the resource into a CreatePropertyCommand.
   *
   * @param resource The CreatePropertyResource containing property creation details.
   * @return The resulting CreatePropertyCommand.
   */
  public static CreatePropertyCommand toCommandFromResource(final CreatePropertyResource resource) {
    var ownerId = new OwnerId(Long.parseLong(resource.ownerId()));

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

    return new CreatePropertyCommand(ownerId, address, region, district);
  }
}