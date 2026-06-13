package com.hampcoders.electrolink.sdp.interfaces.rest.resources;

import java.util.List;

/**
 * Resource representing restrictions.
 *
 * @param unavailableDistricts List of unavailable districts.
 * @param forbiddenDays List of forbidden days.
 * @param requiresSpecialCertification Whether special certification is required.
 */
public record RestrictionResource(
    List<String> unavailableDistricts,
    List<String> forbiddenDays,
    boolean requiresSpecialCertification
) {

}
