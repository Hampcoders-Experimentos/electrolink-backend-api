package com.hampcoders.electrolink.profiles.interfaces.rest.resources;

import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;

/**
 * Resource representing a user profile.
 *
 * @param id                        The unique identifier of the profile.
 * @param firstName                 The first name of the profile.
 * @param lastName                  The last name of the profile.
 * @param email                     The email of the profile.
 * @param street                    The street address of the profile.
 * @param role                      The role of the profile (e.g., CLIENT, TECHNICIAN).
 * @param additionalInfoOrCertification Additional information or certification details,
 *     depending on the role.
 * @param isVerified                Whether the technician is verified.
 */
public record ProfileResource(
    Long id,
    String firstName,
    String lastName,
    String email,
    String street,
    Role role,
    String additionalInfoOrCertification,
    Boolean isVerified
) {}
