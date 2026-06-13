package com.hampcoders.electrolink.assets.interfaces.rest.resources;

/**
 * Resource representing the complete details of a Property.
 */
public record PropertyResource(
    String id,
    String ownerId,
    AddressResource address,
    RegionResource region,
    DistrictResource district
) {
}