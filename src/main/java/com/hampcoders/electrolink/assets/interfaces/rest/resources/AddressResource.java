package com.hampcoders.electrolink.assets.interfaces.rest.resources;

/**
 * Resource representing a physical address.
 */
public record AddressResource(
    String street,
    String number,
    String city,
    String postalCode,
    String country,
    float latitude,
    float longitude
) {}