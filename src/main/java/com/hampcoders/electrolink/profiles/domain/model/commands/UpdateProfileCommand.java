package com.hampcoders.electrolink.profiles.domain.model.commands;

import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;

/**
 * Command object representing the data needed to update an existing profile.
 *
 * @param profileId the ID of the profile to be updated
 * @param firstName the new first name for the profile
 * @param lastName the new last name for the profile
 * @param email the new email address for the profile
 * @param street the new street address for the profile
 * @param role the new role for the profile
 * @param additionalInfoOrCertification any additional information or certification
 *     relevant to the profile, such as a technician's certification details or
 *     a client's preferences
 */
public record UpdateProfileCommand(
    Long profileId,
    String firstName,
    String lastName,
    String email,
    String street,
    Role role,
    String additionalInfoOrCertification
) {}

