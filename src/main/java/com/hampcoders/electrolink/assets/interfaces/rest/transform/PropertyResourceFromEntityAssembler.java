package com.hampcoders.electrolink.assets.interfaces.rest.transform;

import com.hampcoders.electrolink.assets.domain.model.aggregates.Property;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.AddressResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.DistrictResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.PropertyResource;
import com.hampcoders.electrolink.assets.interfaces.rest.resources.RegionResource;

/**
 * Assembler to convert Property entities into PropertyResource.
 */
public class PropertyResourceFromEntityAssembler {
  /**
   * Converts the Property entity to a PropertyResource.
   *
   * @param entity The Property entity.
   * @return The resulting PropertyResource.
   */
  public static PropertyResource toResourceFromEntity(final Property entity) {
    var addressResource = new AddressResource(
        entity.getAddress().street(),
        entity.getAddress().number(),
        entity.getAddress().city(),
        entity.getAddress().postalCode(),
        entity.getAddress().country(),
        entity.getAddress().latitude(),
        entity.getAddress().longitude()
    );

    var regionResource = new RegionResource(entity.getRegion().name());
    var districtResource = new DistrictResource(entity.getDistrict().name());

    return new PropertyResource(
        entity.getId().toString(),
        entity.getOwnerId().ownerId().toString(),
        addressResource,
        regionResource,
        districtResource
    );
  }
}