package com.hampcoders.electrolink.profiles.interfaces.rest.resources;

import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;

/**
 * Resource representing the data required to create a new profile.
 *
 * @param firstName                 The first name of the profile.
 * @param lastName                  The last name of the profile.
 * @param email                     The email of the profile.
 * @param street                    The street address of the profile.
 * @param role                      The role of the profile (e.g., CLIENT, TECHNICIAN).
 * @param additionalInfoOrCertification Additional information or certification details,
 *     depending on the role.
 */
public record CreateProfileResource(
    String firstName,
    String lastName,
    String email,
    String street,
    Role role,
    String additionalInfoOrCertification
) {}

