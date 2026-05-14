package com.hampcoders.electrolink.profiles.domain.model.commands;

import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;

/**
 * Command object representing the data needed to create a new user profile.
 *
 * @param firstName the first name of the user
 * @param lastName the last name of the user
 * @param email the email address of the user
 * @param street the street address of the user
 * @param role the role of the user
 * @param additionalInfoOrCertification any additional information or certification details
 */
public record CreateProfileCommand(
    String firstName,
    String lastName,
    String email,
    String street,
    Role role,
    String additionalInfoOrCertification
) {}

